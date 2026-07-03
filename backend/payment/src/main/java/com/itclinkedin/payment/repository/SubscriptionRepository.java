package com.itclinkedin.payment.repository;

import com.itclinkedin.payment.model.Subscription;
import com.itclinkedin.payment.model.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByIdempotencyKey(String idempotencyKey);

    // ATOMIC claim: flip status ONLY if it's still `from`.
    // Returns rows changed → 1 = we won, 0 = someone else already moved it.
    @Transactional
    @Modifying
    @Query("UPDATE Subscription s SET s.status = :to WHERE s.id = :id AND s.status = :from")
    int claim(@Param("id") Long id,
              @Param("from") SubscriptionStatus from,
              @Param("to") SubscriptionStatus to);
}