CREATE TABLE users (
                       id bigserial PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       bio VARCHAR(500),
                       active_artist_id BIGINT
);