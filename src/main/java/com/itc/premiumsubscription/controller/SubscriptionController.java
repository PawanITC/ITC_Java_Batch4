package com.itc.premiumsubscription.controller;

import com.itc.premiumsubscription.dto.InitiateSubscriptionRequestDTO;
import com.itc.premiumsubscription.dto.SubscriptionResponseDTO;
import com.itc.premiumsubscription.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }
    @PostMapping("/initiate")
    public ResponseEntity<SubscriptionResponseDTO> initiateSubscription(
            @Valid @RequestBody InitiateSubscriptionRequestDTO request) {
        SubscriptionResponseDTO response = subscriptionService.initiateSubscription(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<SubscriptionResponseDTO> getSubscriptionByUser(@PathVariable UUID userId) {
        SubscriptionResponseDTO response = subscriptionService.getSubscriptionByUserId(userId);
        return ResponseEntity.ok(response);
    }
}
