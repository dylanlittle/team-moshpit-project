ALTER TABLE artists
ADD COLUMN IF NOT EXISTS spotify_artist_id VARCHAR(64);

CREATE TABLE IF NOT EXISTS listening_events (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  spotify_artist_id VARCHAR(64) NOT NULL,
  spotify_track_id VARCHAR(64),
  played_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_listening_events_user_artist_time
  ON listening_events(user_id, spotify_artist_id, played_at DESC);
