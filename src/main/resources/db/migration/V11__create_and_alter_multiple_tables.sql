CREATE TABLE concert_goers (
     id BIGSERIAL PRIMARY KEY,
     user_id BIGINT NOT NULL,
     concert_id BIGINT NOT NULL,

     CONSTRAINT fk_concert_goers_user
                            FOREIGN KEY (user_id)
                            REFERENCES users(id)
                            ON DELETE CASCADE,
     CONSTRAINT fk_concert_goers_concert
                            FOREIGN KEY (concert_id)
                            REFERENCES concerts(id)
                            ON DELETE CASCADE,
     CONSTRAINT uq_concert_goer
                            UNIQUE (user_id, concert_id)
);

CREATE TABLE lineup_artists (
     id BIGSERIAL PRIMARY KEY,
     artist_id BIGINT NOT NULL,
     concert_id BIGINT NOT NULL,

     CONSTRAINT fk_lineup_artists_artist
                            FOREIGN KEY (artist_id)
                            REFERENCES artists(id)
                            ON DELETE CASCADE,
     CONSTRAINT fk_lineup_artists_concert
                            FOREIGN KEY (concert_id)
                            REFERENCES concerts(id)
                            ON DELETE CASCADE,
     CONSTRAINT uq_lineup_artist
                            UNIQUE (artist_id, concert_id)
);

ALTER TABLE posts
ADD COLUMN concert_id BIGINT DEFAULT NULL;

INSERT INTO users (name, username, email, bio, active_artist_id, avatar, location) VALUES
    ('Test User', 'testaccount', 'test@testing.com', 'Just here for testing', NULL, NULL, 'London, UK'),
    ('Alice Martell', 'alicem', 'alice@example.com', 'Swiftie 4eva', NULL, NULL, 'Bristol, UK'),
    ('Marcus Chen', 'mchen_drums', 'marcus@testmail.org', 'Take me back to glasto', NULL, NULL, 'Manchester, UK');

INSERT INTO concert_goers (user_id, concert_id) VALUES
    (1, 1),
    (1, 2),
    (1, 4),
    (2, 1),
    (2, 2),
    (2, 3),
    (3, 1),
    (3, 3);


