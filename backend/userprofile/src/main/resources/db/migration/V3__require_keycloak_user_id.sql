ALTER TABLE user_profiles
    ADD COLUMN IF NOT EXISTS keycloak_user_id VARCHAR(255);

UPDATE user_profiles
SET keycloak_user_id = 'legacy-' || CAST(id AS VARCHAR)
WHERE keycloak_user_id IS NULL OR btrim(keycloak_user_id) = '';

ALTER TABLE user_profiles
    ALTER COLUMN keycloak_user_id SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uk_user_profiles_keycloak_user_id
    ON user_profiles (keycloak_user_id);
