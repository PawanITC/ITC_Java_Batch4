package com.itc.premiumsubscription.service;


import com.itc.premiumsubscription.dto.InitiateSubscriptionRequestDTO;
import com.itc.premiumsubscription.dto.SubscriptionResponseDTO;
import com.itc.premiumsubscription.exception.ResourceNotFoundException;
import com.itc.premiumsubscription.model.Subscription;
import com.itc.premiumsubscription.model.SubscriptionStatus;
import com.itc.premiumsubscription.repository.SubscriptionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public SubscriptionResponseDTO initiateSubscription(InitiateSubscriptionRequestDTO request) {
        // Business Rule: Check if user already has an active subscription if required

        Subscription subscription = new Subscription(
                request.userId(),
                request.planId(),
                SubscriptionStatus.PENDING_PAYMENT
        );

        Subscription saved = subscriptionRepository.save(subscription);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public SubscriptionResponseDTO getSubscriptionByUserId(UUID userId) {
        Subscription subscription = subscriptionRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No subscription found for user: " + userId));
        return mapToResponse(subscription);
    }

    private SubscriptionResponseDTO mapToResponse(Subscription sub) {
        return new SubscriptionResponseDTO(
                sub.getId(),
                sub.getUserId(),
                sub.getPlanId(),
                sub.getStatus(),
                sub.getStartDate(),
                sub.getEndDate(),
                sub.getNextBillingDate()
        );
    }
}
