package com.saurabh.paymentgateway.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="authorization_events")
public class AuthorizationEvent {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private UUID authorizationId;
 @Column(nullable=false) private String eventType;
 @Column(nullable=false) private String status;
 @Column(nullable=false) private Instant occurredAt;
 @Column(nullable=false,columnDefinition="jsonb") private String metadata;
 protected AuthorizationEvent() {}
 public AuthorizationEvent(UUID authId,String type,String status,String metadata){this.authorizationId=authId;this.eventType=type;this.status=status;this.occurredAt=Instant.now();this.metadata=metadata;}
}
