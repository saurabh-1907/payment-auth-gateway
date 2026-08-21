package com.saurabh.paymentgateway.config;
import io.swagger.v3.oas.models.OpenAPI;import io.swagger.v3.oas.models.info.Info;import org.springframework.context.annotation.Bean;import org.springframework.context.annotation.Configuration;
@Configuration public class OpenApiConfig {@Bean OpenAPI api(){return new OpenAPI().info(new Info().title("Payment Authorization Gateway").version("v1").description("Card authorization API; PAN is accepted only for authorization and never persisted or logged."));}}
