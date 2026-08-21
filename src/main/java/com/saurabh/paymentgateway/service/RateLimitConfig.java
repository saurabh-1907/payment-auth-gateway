package com.saurabh.paymentgateway.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.codec.ByteArrayCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class RateLimitConfig {
 @Bean RedisClient bucketRedisClient(@Value("${spring.data.redis.host:localhost}") String host,@Value("${spring.data.redis.port:6379}") int port){return RedisClient.create("redis://"+host+":"+port);}
 @Bean LettuceBasedProxyManager<String> bucketProxyManager(RedisClient client){
   return Bucket4jLettuce.casBasedBuilder(client.connect(ByteArrayCodec.INSTANCE)).build();
 }
 @Bean BucketConfiguration bucketConfiguration(@Value("${rate-limit.capacity:100}") long capacity,@Value("${rate-limit.refill-tokens:100}") long tokens){
   return BucketConfiguration.builder().addLimit(Bandwidth.classic(capacity, Refill.greedy(tokens, Duration.ofMinutes(1)))).build();
 }
}
