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

@Service
public class SubscriptionService {

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
            sub.setCurrency(request.currency());
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
        if (r.amount() == null || r.amount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("amount must be greater than 0");
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    // SHA-256 fingerprint of the fields that define "the same request"
    private String hashOf(SubscribeRequest r) {
        String canonical = r.userId() + "|" + r.plan() + "|" + r.amount() + "|" + r.currency();
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(canonical.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException("Could not hash request", e);
        }
    }
}