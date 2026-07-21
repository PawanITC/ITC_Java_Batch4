package com.itclinkedin.payment.service;

import com.itclinkedin.payment.dto.SubscribeRequest;
import com.itclinkedin.payment.exception.IdempotencyConflictException;
import com.itclinkedin.payment.model.Subscription;
import com.itclinkedin.payment.repository.SubscriptionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Currency;

@Service
public class SubscriptionService {

    // server-side price catalog — the frontend's amount is VALIDATED, never trusted
    private static final java.util.Map<String, BigDecimal> PLAN_PRICES = java.util.Map.of(
            "premium-career",   new BigDecimal("29.99"),
            "premium-business", new BigDecimal("59.99")
    );

    private final SubscriptionRepository repository;

    public SubscriptionService(SubscriptionRepository repository) {
        this.repository = repository;
    }

    public Subscription subscribe(SubscribeRequest request) {
        validate(request);
        String hash = hashOf(request);

        // 1) fast path — have we seen this key before?
        var existing = repository.findByIdempotencyKey(request.idempotencyKey());
        if (existing.isPresent()) {
            return replayOrConflict(existing.get(), hash);
        }

        // 2) first time → try to create it
        try {
            Subscription sub = new Subscription();
            sub.setIdempotencyKey(request.idempotencyKey());
            sub.setRequestHash(hash);
            sub.setUserId(request.userId());
            sub.setPlan(request.plan());
            sub.setAmount(request.amount());
            sub.setCurrency(request.currency().toUpperCase());
            // status defaults to PENDING inside the entity
            return repository.saveAndFlush(sub);   // flush NOW → DB checks the unique key immediately
        } catch (DataIntegrityViolationException race) {
            // 3) two requests with the same key raced — the DB rejected us (we're the loser).
            //    Re-read the winner's row and replay it (or 409 if the bodies differ).
            Subscription winner = repository.findByIdempotencyKey(request.idempotencyKey())
                    .orElseThrow(() -> race);
            return replayOrConflict(winner, hash);
        }
    }

    public Subscription getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new com.itclinkedin.payment.exception.NotFoundException(
                        "No subscription with id " + id));
    }

    // same key + same body → safe replay.  same key + DIFFERENT body → 409.
    private Subscription replayOrConflict(Subscription existing, String hash) {
        if (!existing.getRequestHash().equals(hash)) {
            throw new IdempotencyConflictException(
                    "idempotencyKey already used with a different request body");
        }
        return existing;
    }

    private void validate(SubscribeRequest r) {
        if (isBlank(r.idempotencyKey())) throw new IllegalArgumentException("idempotencyKey is required");
        if (isBlank(r.userId()))         throw new IllegalArgumentException("userId is required");
        if (isBlank(r.plan()))           throw new IllegalArgumentException("plan is required");
        if (isBlank(r.currency()))       throw new IllegalArgumentException("currency is required");

        // currency must be a real ISO-4217 code (GBP, USD, EUR…)
        try {
            Currency.getInstance(r.currency().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("currency must be a valid ISO code, got: " + r.currency());
        }

        if (r.amount() == null || r.amount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("amount must be greater than 0");
        if (r.amount().scale() > 2)
            throw new IllegalArgumentException("amount cannot have more than 2 decimal places");
        if (r.amount().compareTo(new BigDecimal("100000")) > 0)
            throw new IllegalArgumentException("amount exceeds the maximum allowed");

        // don't trust the frontend's amount — it must match the server-side plan price
        BigDecimal expected = PLAN_PRICES.get(r.plan());
        if (expected == null)
            throw new IllegalArgumentException("unknown plan: " + r.plan());
        if (expected.compareTo(r.amount()) != 0)
            throw new IllegalArgumentException("amount does not match plan price");
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    // SHA-256 fingerprint of the fields that define "the same request"
    private String hashOf(SubscribeRequest r) {
        String canonical = r.userId() + "|" + r.plan() + "|"
                + r.amount().stripTrailingZeros().toPlainString() + "|"
                + r.currency().toUpperCase();
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(canonical.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException("Could not hash request", e);
        }
    }
}