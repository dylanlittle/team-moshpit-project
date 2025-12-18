ALTER TABLE artists
ADD COLUMN verified BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE artists
ADD COLUMN created_by BIGINT;

ALTER TABLE artists
ADD CONSTRAINT fk_artists_created_by
FOREIGN KEY (created_by)
REFERENCES users(id);

UPDATE artists
SET verified = FALSE;


