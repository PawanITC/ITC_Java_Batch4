CREATE TABLE follows (
    follower_id UUID NOT NULL,
    following_id UUID NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (follower_id, following_id)
) PARTITION BY HASH (follower_id);

CREATE TABLE follows_shard_0 PARTITION OF follows FOR VALUES WITH (MODULUS 4, REMAINDER 0);
CREATE TABLE follows_shard_1 PARTITION OF follows FOR VALUES WITH (MODULUS 4, REMAINDER 1);
CREATE TABLE follows_shard_2 PARTITION OF follows FOR VALUES WITH (MODULUS 4, REMAINDER 2);
CREATE TABLE follows_shard_3 PARTITION OF follows FOR VALUES WITH (MODULUS 4, REMAINDER 3);

-- Covered index mapping reverse tracking straight out of Memory (Index-Only Scan)
CREATE INDEX idx_follows_following_covered ON follows (following_id) INCLUDE (follower_id);