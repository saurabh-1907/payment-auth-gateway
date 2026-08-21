package com.saurabh.paymentgateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saurabh.paymentgateway.api.AuthorizationResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.Optional;

@Component
public class IdempotencyStore {
 private final StringRedisTemplate redis; private final ObjectMapper mapper;
 public IdempotencyStore(StringRedisTemplate redis,ObjectMapper mapper){this.redis=redis;this.mapper=mapper;}
 public Optional<AuthorizationResponse> get(String client,String key){
   try {String v=redis.opsForValue().get(redisKey(client,key));return v==null?Optional.empty():Optional.of(mapper.readValue(v,AuthorizationResponse.class));}
   catch(Exception e){throw new IllegalStateException("idempotency store unavailable",e);}
 }
 public void put(String client,String key,AuthorizationResponse response,Duration ttl){
   try {redis.opsForValue().set(redisKey(client,key),mapper.writeValueAsString(response),ttl);}
   catch(Exception e){throw new IllegalStateException("idempotency store unavailable",e);}
 }
 private String redisKey(String client,String key){return "idempotency:"+client+":"+key;}
}
