package com.itclinkedin.payment.controller;

import com.itclinkedin.payment.model.Subscription;
import com.itclinkedin.payment.model.SubscriptionStatus;
import com.itclinkedin.payment.repository.SubscriptionRepository;
import com.itclinkedin.payment.service.PaymentFinalizer;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class StripeWebhookController {

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    private final SubscriptionRepository repository;
    private final PaymentFinalizer finalizer;

    public StripeWebhookController(SubscriptionRepository repository, PaymentFinalizer finalizer) {
        this.repository = repository;
        this.finalizer = finalizer;
    }

    @PostMapping("/webhooks/stripe")
    public ResponseEntity<String> handle(@RequestBody String payload,
                                         @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;
        try {
            // 🔒 verify the signature — nobody can fake a "payment succeeded" event
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().body("invalid signature");
        }

        if ("payment_intent.succeeded".equals(event.getType())) {
            // API-version tolerant: strict deserialization first, fall back to unsafe.
            // (SDK-pinned API version can differ from the account's event version.)
            PaymentIntent intent;
            try {
                intent = (PaymentIntent) event.getDataObjectDeserializer().getObject()
                        .orElseGet(() -> {
                            try {
                                return (PaymentIntent) event.getDataObjectDeserializer().deserializeUnsafe();
                            } catch (Exception ex) {
                                throw new IllegalStateException("cannot deserialize event", ex);
                            }
                        });
            } catch (Exception e) {
                System.out.println("⚠️ webhook deserialization failed: " + e.getMessage());
                return ResponseEntity.internalServerError().body("deserialization failed — retry");
            }

            Long subId = Long.valueOf(intent.getMetadata().get("subscriptionId"));

            // idempotent: Stripe RETRIES webhooks → the atomic claim wins once, dupes are skipped
            int claimed = repository.claim(subId, SubscriptionStatus.PENDING, SubscriptionStatus.PROCESSING);
            if (claimed == 0) {
                return ResponseEntity.ok("already processed");
            }
            Subscription sub = repository.findById(subId).orElseThrow();
            try {
                finalizer.activate(sub, intent.getId());   // ACTIVE + ledger + outbox, one transaction
                System.out.println("✅ Stripe webhook → subscription " + subId + " ACTIVE");
            } catch (Exception activationFailed) {
                // revert the claim so Stripe's automatic webhook RETRY can process it again
                repository.claim(subId, SubscriptionStatus.PROCESSING, SubscriptionStatus.PENDING);
                return ResponseEntity.internalServerError().body("activation failed — retry");
            }
        }
        return ResponseEntity.ok("received");
    }
}
