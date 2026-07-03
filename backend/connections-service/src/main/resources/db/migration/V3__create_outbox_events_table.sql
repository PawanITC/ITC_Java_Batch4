CREATE TABLE outbox_events (
                               id UUID PRIMARY KEY,

                               aggregate_id UUID NOT NULL,
                               aggregate_type VARCHAR(100) NOT NULL,
                               event_type VARCHAR(100) NOT NULL,

                               payload JSONB NOT NULL,

                               status VARCHAR(30) NOT NULL,

                               created_at TIMESTAMP NOT NULL,
                               published_at TIMESTAMP,

                               CONSTRAINT chk_outbox_status
                                   CHECK (status IN ('PENDING', 'PUBLISHED', 'FAILED'))
);

CREATE INDEX idx_outbox_events_status
    ON outbox_events(status);

CREATE INDEX idx_outbox_events_created_at
    ON outbox_events(created_at);

CREATE INDEX idx_outbox_events_aggregate_id
    ON outbox_events(aggregate_id);