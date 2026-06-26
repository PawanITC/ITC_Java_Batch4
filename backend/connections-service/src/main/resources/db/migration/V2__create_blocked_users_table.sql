CREATE TABLE blocked_users (
                               id UUID PRIMARY KEY,

                               blocker_id UUID NOT NULL,
                               blocked_id UUID NOT NULL,

                               created_at TIMESTAMP NOT NULL,

                               CONSTRAINT chk_no_self_block
                                   CHECK (blocker_id <> blocked_id),

                               CONSTRAINT uq_blocker_blocked
                                   UNIQUE (blocker_id, blocked_id)
);

CREATE INDEX idx_blocked_users_blocker_id
    ON blocked_users(blocker_id);

CREATE INDEX idx_blocked_users_blocked_id
    ON blocked_users(blocked_id);