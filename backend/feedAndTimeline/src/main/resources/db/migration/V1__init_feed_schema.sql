CREATE TABLE IF NOT EXISTS timeline_posts (
    id BIGSERIAL PRIMARY KEY,
    timeline_user_id VARCHAR(255) NOT NULL,
    post_id BIGINT NOT NULL,
    author_id VARCHAR(255),
    author_name VARCHAR(255),
    author_headline VARCHAR(255),
    content VARCHAR(5000),
    likes_count INTEGER NOT NULL DEFAULT 0,
    comments_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_timeline_user_post
    ON timeline_posts (timeline_user_id, post_id);

CREATE INDEX IF NOT EXISTS idx_timeline_user_created
    ON timeline_posts (timeline_user_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_timeline_post_id
    ON timeline_posts (post_id);

CREATE TABLE IF NOT EXISTS processed_events (
    id BIGSERIAL PRIMARY KEY,
    consumer_name VARCHAR(255) NOT NULL,
    topic VARCHAR(255) NOT NULL,
    event_id VARCHAR(255) NOT NULL,
    event_version INTEGER,
    processed_at TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_processed_event_consumer_event
    ON processed_events (consumer_name, event_id);

CREATE INDEX IF NOT EXISTS idx_processed_event_topic
    ON processed_events (topic);

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
