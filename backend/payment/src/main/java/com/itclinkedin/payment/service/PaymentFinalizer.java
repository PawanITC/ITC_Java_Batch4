package com.itclinkedin.payment.service;

import com.itclinkedin.payment.model.LedgerEntry;
import com.itclinkedin.payment.model.OutboxEvent;
import com.itclinkedin.payment.model.Subscription;
import com.itclinkedin.payment.repository.LedgerRepository;
import com.itclinkedin.payment.repository.OutboxRepository;
import com.itclinkedin.payment.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class PaymentFinalizer {

    private final SubscriptionRepository subscriptions;
    private final LedgerRepository ledger;
    private final OutboxRepository outbox;

    public PaymentFinalizer(SubscriptionRepository subscriptions, LedgerRepository ledger, OutboxRepository outbox) {
        this.subscriptions = subscriptions;
        this.ledger = ledger;
        this.outbox = outbox;
    }

    // ledger + status + outbox ALL commit together — or none do.
    @Transactional
    public Subscription activate(Subscription sub, String chargeRef) {
        ledger.save(LedgerEntry.succeeded(sub, chargeRef));          // money row
        sub.activate();                                             // PROCESSING → ACTIVE
        sub.setStripeSubscriptionId(chargeRef);
        sub.setCurrentPeriodEnd(Instant.now().plus(30, ChronoUnit.DAYS));
        Subscription saved = subscriptions.save(sub);

        String payload = String.format(
                "{\"subscriptionId\":%d,\"userId\":\"%s\",\"plan\":\"%s\"}",
                sub.getId(), sub.getUserId(), sub.getPlan());
        outbox.save(OutboxEvent.pending("subscription.activated", String.valueOf(sub.getId()), payload));

        return saved;
    }

    @Transactional
    public void cancel(Subscription sub) {
        ledger.save(LedgerEntry.declined(sub));
        sub.cancel();
        subscriptions.save(sub);
    }
}