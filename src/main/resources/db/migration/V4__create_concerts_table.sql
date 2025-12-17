CREATE TABLE concerts (
                       id bigserial PRIMARY KEY,
                       date DATE NOT NULL,
                       city VARCHAR(255) NOT NULL UNIQUE,
                       country VARCHAR(255) NOT NULL UNIQUE,
                       venue VARCHAR(255) NOT NULL,
                       artist_id BIGINT,
                       CONSTRAINT fk_artist
                           FOREIGN KEY (artist_id)
                               REFERENCES artists (id)
                               ON DELETE CASCADE
);

INSERT INTO concerts (date, city, country, venue, artist_id)
VALUES
    (2026-03-17, 'Amsterdam', 'Netherlands', 'Paradiso', 1),
    (2026-03-18, 'Cologne', 'Germany', 'Palladium', 1),
    (2026-03-20, 'Bristol', 'UK', 'The Prospect Building', 1),
    (2026-03-21, 'Glasgow', 'UK', 'SWG3 Studio Warehouse', 1),
    (2026-03-22, 'Leeds', 'UK', 'O2 Academy Leeds', 1),
    (2026-03-24, 'Manchester', 'UK', 'O2 Victoria Warehouse', 1),
    (2026-03-25, 'London', 'UK', 'Eventim Apollo', 1),
    (2026-06-20, 'Brussles', 'Belgium', 'Ancienne Belgique', 3);
