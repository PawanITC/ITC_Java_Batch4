-- V2: idempotency hardening — request_hash + NOT NULL guards

-- add request_hash NULLABLE first (works even if rows already exist)
ALTER TABLE subscriptions ADD COLUMN request_hash VARCHAR(64);

-- backfill any old rows so we can safely enforce NOT NULL
UPDATE subscriptions SET request_hash = 'legacy' WHERE request_hash IS NULL;

-- now enforce: every subscription MUST have a hash AND a key
ALTER TABLE subscriptions ALTER COLUMN request_hash    SET NOT NULL;
ALTER TABLE subscriptions ALTER COLUMN idempotency_key SET NOT NULL;