package com.saurabh.paymentgateway.api;

import com.saurabh.paymentgateway.service.AuthorizationService;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
 @ExceptionHandler(AuthorizationService.NotFoundException.class) ResponseEntity<?> notFound(){return ResponseEntity.status(404).body(Map.of("error","NOT_FOUND"));}
 @ExceptionHandler(AuthorizationService.InvalidStateException.class) ResponseEntity<?> invalid(){return ResponseEntity.status(409).body(Map.of("error","INVALID_STATE"));}
 @ExceptionHandler(AuthorizationController.RateLimitExceededException.class) ResponseEntity<?> limited(){return ResponseEntity.status(429).body(Map.of("error","RATE_LIMITED"));}
 @ExceptionHandler(IllegalStateException.class) ResponseEntity<?> dependency(){return ResponseEntity.status(503).body(Map.of("error","DEPENDENCY_UNAVAILABLE"));}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<?> validation(MethodArgumentNotValidException e){return ResponseEntity.badRequest().body(Map.of("error","VALIDATION_FAILED"));}
 @ExceptionHandler(ConstraintViolationException.class) ResponseEntity<?> constraint(){return ResponseEntity.badRequest().body(Map.of("error","VALIDATION_FAILED"));}
 @ExceptionHandler(Exception.class) ResponseEntity<?> generic(){return ResponseEntity.status(500).body(Map.of("error","INTERNAL_ERROR"));}
}
