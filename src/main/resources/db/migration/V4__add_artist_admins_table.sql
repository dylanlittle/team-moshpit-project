CREATE TYPE artist_role as enum ('OWNER', 'ADMIN');

CREATE TABLE artist_admins (
                               artist_id bigint not null references artists(id) on delete cascade,
                               user_id bigint not null references users(id) on delete cascade,
                               role artist_role not null,
                               created_at timestamptz not null default now(),
                               created_by_user_id bigint references users(id),
                               primary key (artist_id, user_id)
);

CREATE INDEX idx_artist_admins_user on artist_admins(user_id);
CREATE INDEX idx_artist_admins_artist on artist_admins(artist_id);

ALTER TABLE users ADD COLUMN auth0_sub text NOT NULL UNIQUE;
ALTER TABLE users ADD COLUMN avatar_url text;

ALTER TABLE posts ADD COLUMN author_user_id BIGINT NOT NULL REFERENCES users(id);


