CREATE TABLE artist_admins (
    id BIGSERIAL PRIMARY KEY,
    artist_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'OWNER',
    CONSTRAINT fk_artist_admins_artist
                           FOREIGN KEY (artist_id)
                           REFERENCES artists(id)
                           ON DELETE CASCADE,
    CONSTRAINT fk_artist_admins_user
                           FOREIGN KEY (user_id)
                           REFERENCES users(id)
                           ON DELETE CASCADE,
    CONSTRAINT uq_artist_admin
                           UNIQUE (artist_id, user_id)
);

CREATE INDEX idx_artist_admins_artist_user ON artist_admins (artist_id, user_id);