ALTER TABLE timeline_posts
    ADD COLUMN IF NOT EXISTS author_avatar_url VARCHAR(512);
