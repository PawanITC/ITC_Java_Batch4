CREATE TABLE IF NOT EXISTS follow_edges (
    id BIGSERIAL PRIMARY KEY,
    follower_id VARCHAR(255) NOT NULL,
    followee_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_follow_edge_follower_followee
    ON follow_edges (follower_id, followee_id);

CREATE INDEX IF NOT EXISTS idx_follow_edge_followee
    ON follow_edges (followee_id);

CREATE INDEX IF NOT EXISTS idx_follow_edge_follower
    ON follow_edges (follower_id);
