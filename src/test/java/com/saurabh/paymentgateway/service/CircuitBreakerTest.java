package com.saurabh.paymentgateway.service;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.junit.jupiter.api.Test;
import java.util.function.Supplier;
import static org.junit.jupiter.api.Assertions.*;
class CircuitBreakerTest {
 @Test void opensAfterConfiguredFailureThreshold(){
  var cb=CircuitBreaker.of("test",io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom().slidingWindowSize(4).minimumNumberOfCalls(4).failureRateThreshold(50).build());
  Supplier<String> call=CircuitBreaker.decorateSupplier(cb,()->{throw new RuntimeException("issuer");});
  for(int i=0;i<4;i++)assertThrows(Exception.class,call::get);assertEquals(CircuitBreaker.State.OPEN,cb.getState());assertThrows(io.github.resilience4j.circuitbreaker.CallNotPermittedException.class,call::get);
 }
}
