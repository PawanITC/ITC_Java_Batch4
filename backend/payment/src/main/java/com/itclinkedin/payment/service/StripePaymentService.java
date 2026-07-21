package com.itclinkedin.payment.service;

import com.itclinkedin.payment.exception.NotFoundException;
import com.itclinkedin.payment.model.Subscription;
import com.itclinkedin.payment.model.SubscriptionStatus;
import com.itclinkedin.payment.repository.SubscriptionRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.stereotype.Service;

@Service
public class StripePaymentService {

    private final SubscriptionRepository repository;

    public StripePaymentService(SubscriptionRepository repository) {
        this.repository = repository;
    }

    // creates a Stripe PaymentIntent → returns the client_secret for the frontend (Stripe.js)
    public String createPaymentIntent(Long subscriptionId) throws StripeException {
        Subscription sub = repository.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException("No subscription with id " + subscriptionId));

        // only a PENDING subscription is payable — no PaymentIntents for ACTIVE/CANCELED subs
        if (sub.getStatus() != SubscriptionStatus.PENDING) {
            throw new IllegalStateException("Subscription is not payable, it is " + sub.getStatus());
        }

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(sub.getAmount().movePointRight(2).longValueExact())     // pounds → pence
                .setCurrency(sub.getCurrency().toLowerCase())
                .putMetadata("subscriptionId", String.valueOf(sub.getId()))         // the webhook reads this
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build())
                .build();

        // provider-level idempotency (2nd defense layer — Stripe won't double-create)
        RequestOptions options = RequestOptions.builder()
                .setIdempotencyKey("pi-" + sub.getIdempotencyKey())
                .build();

        PaymentIntent intent = PaymentIntent.create(params, options);

        // store the PaymentIntent id at CREATION — reconcilable even if the user abandons checkout
        sub.setStripeSubscriptionId(intent.getId());
        repository.save(sub);

        return intent.getClientSecret();
    }
}
