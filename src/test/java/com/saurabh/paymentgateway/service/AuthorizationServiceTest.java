package com.saurabh.paymentgateway.service;

import com.saurabh.paymentgateway.api.CreateAuthorizationRequest;
import com.saurabh.paymentgateway.repository.AuthorizationEventRepository;
import com.saurabh.paymentgateway.repository.AuthorizationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import static org.junit.jupiter.api.Assertions.*;

class AuthorizationServiceTest {
 @Test void authorizeMasksPanAndPersistsLast4Only(){
  var repo=Mockito.mock(AuthorizationRepository.class);var events=Mockito.mock(AuthorizationEventRepository.class);var issuer=Mockito.mock(IssuerClient.class);var idem=Mockito.mock(IdempotencyStore.class);
  Mockito.when(idem.get("client","key-12345")).thenReturn(java.util.Optional.empty());Mockito.when(issuer.authorize(1200)).thenReturn(CompletableFuture.completedFuture(new IssuerClient.IssuerResult(true,"APPROVED")));
  var s=new AuthorizationService(repo,events,issuer,idem,Duration.ofHours(1));var r=s.authorize("client","key-12345",new CreateAuthorizationRequest(1200L,"INR","4111111111111111"));
  assertEquals("**** **** **** 1111",r.maskedPan());assertEquals("AUTHORIZED",r.status());Mockito.verify(repo).save(Mockito.argThat(a->a.getPanLast4().equals("1111")));Mockito.verify(idem).put(Mockito.eq("client"),Mockito.eq("key-12345"),Mockito.any(),Mockito.any());
 }
 @Test void issuerFailureIsMappedToDecline(){
  var repo=Mockito.mock(AuthorizationRepository.class);var events=Mockito.mock(AuthorizationEventRepository.class);var issuer=Mockito.mock(IssuerClient.class);var idem=Mockito.mock(IdempotencyStore.class);Mockito.when(idem.get(Mockito.any(),Mockito.any())).thenReturn(java.util.Optional.empty());Mockito.when(issuer.authorize(99999)).thenReturn(CompletableFuture.failedFuture(new IssuerClient.IssuerUnavailableException("down")));
  var s=new AuthorizationService(repo,events,issuer,idem,Duration.ofHours(1));assertThrows(IssuerClient.IssuerUnavailableException.class,()->s.authorize("client","key-12345",new CreateAuthorizationRequest(99999L,"INR","4111111111111111")));Mockito.verify(repo).save(Mockito.argThat(a->a.getStatus().name().equals("DECLINED")));
 }
}
