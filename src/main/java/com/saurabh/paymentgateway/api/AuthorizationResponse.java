package com.saurabh.paymentgateway.api;
import java.util.UUID;
public record AuthorizationResponse(UUID id,String status,long amountMinor,String currency,String maskedPan,String issuerCode) {}
