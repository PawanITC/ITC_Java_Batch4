package com.itclinkedin.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IdempotencyConflictException.class)
    public ResponseEntity<Map<String, String>> handleConflict(IdempotencyConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)            // 409
                .body(Map.of("error", "idempotency_conflict", "message", ex.getMessage()));
    }

    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<Map<String, String>> handlePaymentFailed(PaymentFailedException ex) {
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)    // 402
                .body(Map.of("error", "payment_failed", "message", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)            // 409
                .body(Map.of("error", "invalid_state", "message", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)         // 400
                .body(Map.of("error", "bad_request", "message", ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)          // 404
                .body(Map.of("error", "not_found", "message", ex.getMessage()));
    }

    @ExceptionHandler(ProviderUnavailableException.class)
    public ResponseEntity<Map<String, String>> handleProviderDown(ProviderUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)   // 503
                .body(Map.of("error", "provider_unavailable", "message", ex.getMessage()));
    }

    @ExceptionHandler(com.stripe.exception.StripeException.class)
    public ResponseEntity<Map<String, String>> handleStripe(com.stripe.exception.StripeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)           // 502
                .body(Map.of("error", "stripe_error", "message", ex.getMessage()));
    }
}