CREATE TABLE outbox_events (
    id           BIGSERIAL PRIMARY KEY,
    event_type   VARCHAR(100)  NOT NULL,   -- subscription.activated
    aggregate_id VARCHAR(255)  NOT NULL,   -- which subscription
    payload      VARCHAR(1000) NOT NULL,   -- JSON the consumer reads
    status       VARCHAR(20)   NOT NULL,   -- PENDING / SENT
    created_at   TIMESTAMP     NOT NULL
);