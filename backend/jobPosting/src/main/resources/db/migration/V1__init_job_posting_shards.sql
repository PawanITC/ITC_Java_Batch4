-- Base Parent Table (Declarative Partitioning by HASH)
CREATE TABLE job_posts (
    id UUID NOT NULL,
    company_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    location VARCHAR(255) NOT NULL,
    salary_min NUMERIC(15, 2),
    salary_max NUMERIC(15, 2),
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (company_id, id)
) PARTITION BY HASH (company_id);

CREATE TABLE job_posts_shard_0 PARTITION OF job_posts FOR VALUES WITH (MODULUS 4, REMAINDER 0);
CREATE TABLE job_posts_shard_1 PARTITION OF job_posts FOR VALUES WITH (MODULUS 4, REMAINDER 1);
CREATE TABLE job_posts_shard_2 PARTITION OF job_posts FOR VALUES WITH (MODULUS 4, REMAINDER 2);
CREATE TABLE job_posts_shard_3 PARTITION OF job_posts FOR VALUES WITH (MODULUS 4, REMAINDER 3);

-- 5NF Normalized Sub-Table for Job Requirements (Decoupled to satisfy Join Dependency validation)
CREATE TABLE job_requirements (
    id UUID PRIMARY KEY,
    job_id UUID NOT NULL,
    company_id UUID NOT NULL,
    requirement VARCHAR(500) NOT NULL,
    is_mandatory BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (company_id, job_id) REFERENCES job_posts(company_id, id) ON DELETE CASCADE
);

-- 5NF Normalized Sub-Table for Job Benefits
CREATE TABLE job_benefits (
    id UUID PRIMARY KEY,
    job_id UUID NOT NULL,
    company_id UUID NOT NULL,
    benefit VARCHAR(255) NOT NULL,
    FOREIGN KEY (company_id, job_id) REFERENCES job_posts(company_id, id) ON DELETE CASCADE
);

-- High Performance Indexing Structures across Shards
CREATE INDEX idx_job_posts_status ON job_posts(status);
CREATE INDEX idx_job_posts_title ON job_posts(title);