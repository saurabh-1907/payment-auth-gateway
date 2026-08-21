package com.saurabh.paymentgateway.integration;

import com.saurabh.paymentgateway.service.IssuerClient;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.concurrent.CompletableFuture;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest @AutoConfigureMockMvc @Testcontainers
class AuthorizationIntegrationTest {
 @Container static PostgreSQLContainer<?> postgres=new PostgreSQLContainer<>("postgres:17-alpine").withDatabaseName("payments").withUsername("payments").withPassword("payments");
 @Container static GenericContainer<?> redis=new GenericContainer<>("redis:7.4-alpine").withExposedPorts(6379);
 @DynamicPropertySource static void props(DynamicPropertyRegistry r){r.add("DB_URL",postgres::getJdbcUrl);r.add("DB_USERNAME",postgres::getUsername);r.add("DB_PASSWORD",postgres::getPassword);r.add("spring.data.redis.host",redis::getHost);r.add("spring.data.redis.port",()->redis.getMappedPort(6379));}
 @Autowired MockMvc mvc; @MockBean JwtDecoder jwtDecoder; @MockBean IssuerClient issuer;
 private final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor auth=jwt().authorities(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_PAYMENT_CLIENT")).jwt(j->j.subject("client-1"));
 @Test void repeatedIdempotencyKeyReplaysOriginalResponse() throws Exception {
  Mockito.when(issuer.authorize(1500)).thenReturn(CompletableFuture.completedFuture(new IssuerClient.IssuerResult(true,"APPROVED")));
  var body="{\"amountMinor\":1500,\"currency\":\"INR\",\"pan\":\"4111111111111111\"}";
  var first=mvc.perform(post("/v1/authorizations").with(auth).header("Idempotency-Key","idem-test-123").contentType(APPLICATION_JSON).content(body)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
  var second=mvc.perform(post("/v1/authorizations").with(auth).header("Idempotency-Key","idem-test-123").contentType(APPLICATION_JSON).content(body)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
  Assertions.assertEquals(first,second);Mockito.verify(issuer,Mockito.times(1)).authorize(1500);
 }
 @Test void malformedRequestIsRejected() throws Exception {mvc.perform(post("/v1/authorizations").with(auth).header("Idempotency-Key","idem-test-456").contentType(APPLICATION_JSON).content("{\"amountMinor\":-1,\"currency\":\"inr\",\"pan\":\"123\"}" )).andExpect(status().isBadRequest());}
}
