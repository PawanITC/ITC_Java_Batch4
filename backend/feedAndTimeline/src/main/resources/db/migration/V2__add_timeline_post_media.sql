ALTER TABLE timeline_posts
    ADD COLUMN IF NOT EXISTS media_object_key VARCHAR(2048),
    ADD COLUMN IF NOT EXISTS media_type VARCHAR(32);
