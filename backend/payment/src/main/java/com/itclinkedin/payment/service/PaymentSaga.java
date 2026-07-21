package com.itclinkedin.payment.service;

import com.itclinkedin.payment.exception.NotFoundException;
import com.itclinkedin.payment.exception.PaymentFailedException;
import com.itclinkedin.payment.exception.ProviderUnavailableException;
import com.itclinkedin.payment.model.Subscription;
import com.itclinkedin.payment.model.SubscriptionStatus;
import com.itclinkedin.payment.provider.ChargeResult;
import com.itclinkedin.payment.provider.PaymentProvider;
import com.itclinkedin.payment.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentSaga {

    private final SubscriptionRepository repository;
    private final PaymentProvider provider;
    private final PaymentFinalizer finalizer;

    public PaymentSaga(SubscriptionRepository repository, PaymentProvider provider, PaymentFinalizer finalizer) {
        this.repository = repository;
        this.provider = provider;
        this.finalizer = finalizer;
    }

    public Subscription pay(Long subscriptionId) {
        // 1) ATOMIC CLAIM: PENDING → PROCESSING. Only ONE concurrent caller wins.
        int claimed = repository.claim(
                subscriptionId, SubscriptionStatus.PENDING, SubscriptionStatus.PROCESSING);

        if (claimed == 0) {
            Subscription existing = repository.findById(subscriptionId).orElse(null);
            if (existing == null)
                throw new NotFoundException("No subscription with id " + subscriptionId);
            throw new IllegalStateException("Subscription is not PENDING, it is " + existing.getStatus());
        }

        Subscription sub = repository.findById(subscriptionId).orElseThrow();

        // 2) charge the card — OUTSIDE any DB transaction (retry + circuit breaker on the provider)
        ChargeResult charge;
        try {
            charge = provider.charge(
                    sub.getUserId(), sub.getAmount(), sub.getCurrency(), sub.getIdempotencyKey());
        } catch (Exception providerDown) {
            // transient: provider errored OR circuit is open → revert PROCESSING→PENDING so it's retryable
            repository.claim(subscriptionId, SubscriptionStatus.PROCESSING, SubscriptionStatus.PENDING);
            throw new ProviderUnavailableException("Payment provider unavailable — please retry");
        }

        // 3a) declined → ledger(DECLINED) + CANCELED (one transaction)
        if (!charge.success()) {
            finalizer.cancel(sub);
            throw new PaymentFailedException("Payment declined: " + charge.message());
        }

        // 3b) charged → ledger + ACTIVE + outbox event (ONE transaction)
        try {
            return finalizer.activate(sub, charge.reference());
        } catch (Exception activationFailed) {
            // the whole finalize tx rolled back → nothing committed → refund & cancel
            provider.refund(charge.reference());
            Subscription fresh = repository.findById(subscriptionId).orElseThrow();
            fresh.cancel();
            repository.save(fresh);
            throw new PaymentFailedException("Activation failed after charge — payment refunded");
        }
    }
}