CREATE TABLE subscriptions (
    id                     BIGSERIAL PRIMARY KEY,
    idempotency_key        VARCHAR(255) UNIQUE,
    user_id                VARCHAR(255),
    plan                   VARCHAR(255),
    status                 VARCHAR(50) NOT NULL,
    amount                 NUMERIC(19, 2),
    currency               VARCHAR(10),
    stripe_subscription_id VARCHAR(255),
    current_period_end     TIMESTAMP,
    created_at             TIMESTAMP
);