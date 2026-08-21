package com.saurabh.paymentgateway.api;
import jakarta.validation.constraints.*;
public record CreateAuthorizationRequest(@NotNull @Positive Long amountMinor,@NotBlank @Pattern(regexp="[A-Z]{3}") String currency,@NotBlank @Pattern(regexp="\\d{12,19}") String pan) {}
