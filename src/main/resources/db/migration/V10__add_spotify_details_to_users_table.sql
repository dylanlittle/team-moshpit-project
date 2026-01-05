ALTER TABLE users
    ADD COLUMN auth0_sub VARCHAR(128),
    ADD COLUMN spotify_user_id VARCHAR(64),
    ADD COLUMN spotify_access_token TEXT,
    ADD COLUMN spotify_refresh_token TEXT,
    ADD COLUMN spotify_token_expires_at TIMESTAMP;

CREATE UNIQUE INDEX IF NOT EXISTS users_auth0_sub_unique ON users(auth0_sub);
