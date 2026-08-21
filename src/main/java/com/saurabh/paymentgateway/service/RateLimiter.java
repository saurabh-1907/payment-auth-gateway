package com.saurabh.paymentgateway.service;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import org.springframework.stereotype.Component;
@Component
public class RateLimiter {
 private final LettuceBasedProxyManager<String> manager; private final BucketConfiguration configuration;
 public RateLimiter(LettuceBasedProxyManager<String> manager,BucketConfiguration configuration){this.manager=manager;this.configuration=configuration;}
 public boolean allow(String clientId){Bucket b=manager.getProxy("rate:"+clientId,()->configuration);return b.tryConsume(1);}
}
