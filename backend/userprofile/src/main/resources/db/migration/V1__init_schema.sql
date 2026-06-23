CREATE TABLE user_profiles (
    id UUID PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255),
    headline VARCHAR(255),
    gender VARCHAR(50),
    about VARCHAR(5000),
    city VARCHAR(255),
    country VARCHAR(255),
    profile_picture_url VARCHAR(255),
    cover_photo_url VARCHAR(255),
    industry VARCHAR(255),
    current_company VARCHAR(255),
    current_position VARCHAR(255),
    website VARCHAR(255),
    github_url VARCHAR(255),
    linkedin_url VARCHAR(255),
    open_to_work BOOLEAN DEFAULT FALSE,
    profile_public BOOLEAN DEFAULT TRUE
);

CREATE TABLE experiences (
    id UUID PRIMARY KEY,
    company_name VARCHAR(255),
    title VARCHAR(255),
    start_date DATE,
    end_date DATE,
    current BOOLEAN,
    description VARCHAR(3000),
    profile_id UUID,
    CONSTRAINT fk_experience_profile FOREIGN KEY (profile_id)
        REFERENCES user_profiles(id) ON DELETE CASCADE
);

CREATE TABLE educations (
    id UUID PRIMARY KEY,
    school_name VARCHAR(255),
    degree VARCHAR(255),
    field_of_study VARCHAR(255),
    start_year INTEGER,
    end_year INTEGER,
    profile_id UUID,
    CONSTRAINT fk_education_profile FOREIGN KEY (profile_id)
        REFERENCES user_profiles(id) ON DELETE CASCADE
);

CREATE TABLE skills (
    id UUID PRIMARY KEY,
    skill_name VARCHAR(255),
    endorsement_count INTEGER,
    profile_id UUID,
    CONSTRAINT fk_skill_profile FOREIGN KEY (profile_id)
        REFERENCES user_profiles(id) ON DELETE CASCADE
);

CREATE TABLE languages (
    id UUID PRIMARY KEY,
    language_name VARCHAR(255),
    proficiency VARCHAR(255),
    profile_id UUID,
    CONSTRAINT fk_language_profile FOREIGN KEY (profile_id)
        REFERENCES user_profiles(id) ON DELETE CASCADE
);