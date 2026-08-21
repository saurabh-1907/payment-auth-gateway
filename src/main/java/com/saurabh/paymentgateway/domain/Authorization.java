package com.saurabh.paymentgateway.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="authorizations")
public class Authorization {
    @Id private UUID id;
    @Column(nullable=false) private String clientId;
    @Column(nullable=false) private long amountMinor;
    @Column(nullable=false, length=3) private String currency;
    @Column(nullable=false, length=4) private String panLast4;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private AuthorizationStatus status;
    private String issuerCode;
    @Column(nullable=false) private Instant createdAt;
    @Column(nullable=false) private Instant updatedAt;
    @Version private long version;
    protected Authorization() {}
    public Authorization(UUID id,String clientId,long amountMinor,String currency,String panLast4){this.id=id;this.clientId=clientId;this.amountMinor=amountMinor;this.currency=currency;this.panLast4=panLast4;this.status=AuthorizationStatus.AUTHORIZED;this.createdAt=Instant.now();this.updatedAt=createdAt;}
    public UUID getId(){return id;} public String getClientId(){return clientId;} public long getAmountMinor(){return amountMinor;} public String getCurrency(){return currency;} public String getPanLast4(){return panLast4;} public AuthorizationStatus getStatus(){return status;} public String getIssuerCode(){return issuerCode;} public Instant getCreatedAt(){return createdAt;} public Instant getUpdatedAt(){return updatedAt;}
    public void decline(String code){status=AuthorizationStatus.DECLINED;issuerCode=code;updatedAt=Instant.now();}
    public void capture(){status=AuthorizationStatus.CAPTURED;updatedAt=Instant.now();}
    public void voidAuthorization(){status=AuthorizationStatus.VOIDED;updatedAt=Instant.now();}
}
