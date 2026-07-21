-- V5: one ledger row per provider charge — the same charge can never be recorded twice.
-- Partial index: DECLINED rows have NULL reference, and NULLs are allowed to repeat.
CREATE UNIQUE INDEX uq_ledger_provider_reference
    ON ledger_entries (provider_reference)
    WHERE provider_reference IS NOT NULL;
