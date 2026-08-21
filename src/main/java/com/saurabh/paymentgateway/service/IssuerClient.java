package com.saurabh.paymentgateway.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Component
public class IssuerClient {
 @CircuitBreaker(name="issuer") @Retry(name="issuer") @TimeLimiter(name="issuer")
 public CompletableFuture<IssuerResult> authorize(long amountMinor) {
   return CompletableFuture.supplyAsync(() -> {
     if (amountMinor == 99999) throw new CompletionException(new IssuerUnavailableException("simulated issuer failure"));
     if (amountMinor == 0) throw new CompletionException(new IssuerUnavailableException("invalid amount"));
     return new IssuerResult(true,"APPROVED");
   });
 }
 public record IssuerResult(boolean approved,String code) {}
 public static class IssuerUnavailableException extends RuntimeException { public IssuerUnavailableException(String m){super(m);} }
}
