package com.saurabh.paymentgateway.repository;
import com.saurabh.paymentgateway.domain.Authorization;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface AuthorizationRepository extends JpaRepository<Authorization,UUID> {}
