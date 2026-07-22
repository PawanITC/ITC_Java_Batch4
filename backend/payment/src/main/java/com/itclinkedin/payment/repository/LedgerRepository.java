package com.itclinkedin.payment.repository;

import com.itclinkedin.payment.model.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LedgerRepository extends JpaRepository<LedgerEntry, Long> {
    List<LedgerEntry> findBySubscriptionId(Long subscriptionId);
}