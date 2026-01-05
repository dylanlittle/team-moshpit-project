DROP TABLE IF EXISTS follows;

CREATE TABLE follows (
    id bigserial PRIMARY KEY,
    user_id BIGINT NOT NULL,
    artist_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_user_artist UNIQUE (user_id, artist_id),
    CONSTRAINT fk_user_id
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_artist_id
        FOREIGN KEY (artist_id)
        REFERENCES artists(id)
        ON DELETE CASCADE
);