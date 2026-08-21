package com.saurabh.paymentgateway.repository;
import com.saurabh.paymentgateway.domain.AuthorizationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AuthorizationEventRepository extends JpaRepository<AuthorizationEvent,Long> {}
