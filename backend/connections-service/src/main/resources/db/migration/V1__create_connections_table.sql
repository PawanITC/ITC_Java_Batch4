CREATE TABLE connections (
                             id UUID PRIMARY KEY,

                             requester_id UUID NOT NULL,
                             receiver_id UUID NOT NULL,

                             status VARCHAR(30) NOT NULL,

                             requested_at TIMESTAMP NOT NULL,
                             responded_at TIMESTAMP,

                             created_at TIMESTAMP NOT NULL,
                             updated_at TIMESTAMP NOT NULL,

                             CONSTRAINT chk_no_self_connection
                                 CHECK (requester_id <> receiver_id),

                             CONSTRAINT chk_connection_status
                                 CHECK (status IN (
                                                   'PENDING',
                                                   'ACCEPTED',
                                                   'REJECTED',
                                                   'CANCELLED',
                                                   'REMOVED'
                                     ))
);
CREATE INDEX idx_connections_requester_id
    ON connections(requester_id);

CREATE INDEX idx_connections_receiver_id
    ON connections(receiver_id);

CREATE INDEX idx_connections_status
    ON connections(status);

CREATE INDEX idx_connections_requester_status
    ON connections(requester_id, status);

CREATE INDEX idx_connections_receiver_status
    ON connections(receiver_id, status);


CREATE UNIQUE INDEX uq_active_connection_pair
    ON connections (
                    LEAST(requester_id, receiver_id),
                    GREATEST(requester_id, receiver_id)
        )
    WHERE status IN ('PENDING', 'ACCEPTED');
