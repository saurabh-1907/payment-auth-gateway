package com.saurabh.paymentgateway.service;

import com.saurabh.paymentgateway.api.*;
import com.saurabh.paymentgateway.domain.*;
import com.saurabh.paymentgateway.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletionException;

@Service
public class AuthorizationService {
 private final AuthorizationRepository repository; private final AuthorizationEventRepository events; private final IssuerClient issuer; private final IdempotencyStore idempotency; private final Duration ttl;
 public AuthorizationService(AuthorizationRepository repository,AuthorizationEventRepository events,IssuerClient issuer,IdempotencyStore idempotency,@Value("${idempotency.ttl:24h}") Duration ttl){this.repository=repository;this.events=events;this.issuer=issuer;this.idempotency=idempotency;this.ttl=ttl;}
 @Transactional
 public AuthorizationResponse authorize(String client,String key,CreateAuthorizationRequest req){
   var replay=idempotency.get(client,key); if(replay.isPresent()) return replay.get();
   String last4=req.pan().substring(req.pan().length()-4); UUID id=UUID.randomUUID(); Authorization a=new Authorization(id,client,req.amountMinor(),req.currency(),last4);
   try {var r=issuer.authorize(req.amountMinor()).join(); if(!r.approved()){a.decline(r.code());} repository.save(a); events.save(new AuthorizationEvent(id,"AUTHORIZATION",a.getStatus().name(),"{}"));
     var response=toResponse(a); idempotency.put(client,key,response,ttl); return response;
   } catch(CompletionException ex){a.decline("ISSUER_UNAVAILABLE");repository.save(a);events.save(new AuthorizationEvent(id,"AUTHORIZATION",a.getStatus().name(),"{}"));throw new IssuerClient.IssuerUnavailableException("issuer unavailable");}
 }
 @Transactional public AuthorizationResponse capture(UUID id){Authorization a=repository.findById(id).orElseThrow(()->new NotFoundException()); if(a.getStatus()!=AuthorizationStatus.AUTHORIZED) throw new InvalidStateException(); a.capture();events.save(new AuthorizationEvent(id,"CAPTURE",a.getStatus().name(),"{}"));return toResponse(a);}
 @Transactional public AuthorizationResponse voidAuthorization(UUID id){Authorization a=repository.findById(id).orElseThrow(NotFoundException::new);if(a.getStatus()!=AuthorizationStatus.AUTHORIZED)throw new InvalidStateException();a.voidAuthorization();events.save(new AuthorizationEvent(id,"VOID",a.getStatus().name(),"{}"));return toResponse(a);}
 @Transactional(readOnly=true) public AuthorizationResponse get(UUID id){return repository.findById(id).map(this::toResponse).orElseThrow(NotFoundException::new);}
 private AuthorizationResponse toResponse(Authorization a){return new AuthorizationResponse(a.getId(),a.getStatus().name(),a.getAmountMinor(),a.getCurrency(),"**** **** **** "+a.getPanLast4(),a.getIssuerCode());}
 public static class NotFoundException extends RuntimeException{} public static class InvalidStateException extends RuntimeException{}
}
