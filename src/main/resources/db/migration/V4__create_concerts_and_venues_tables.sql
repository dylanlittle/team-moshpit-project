DROP TABLE IF EXISTS venues;
DROP TABLE IF EXISTS concerts;


CREATE TABLE venues (
                       id bigserial PRIMARY KEY,
                       venue_name VARCHAR(255) NOT NULL,
                       city VARCHAR(255) NOT NULL,
                       country VARCHAR(255) NOT NULL,
                       address VARCHAR(255) NOT NULL
);

CREATE TABLE concerts (
                       id bigserial PRIMARY KEY,
                       concert_date DATE NOT NULL,
                       venue_id BIGINT NOT NULL,
                       artist_id BIGINT NOT NULL,
                       CONSTRAINT fk_venue
                          FOREIGN KEY (venue_id)
                              REFERENCES venues (id)
                              ON DELETE RESTRICT,
                       CONSTRAINT fk_artist
                           FOREIGN KEY (artist_id)
                               REFERENCES artists (id)
                               ON DELETE RESTRICT
);

INSERT INTO venues (venue_name, city, country, address) VALUES
('Paradiso', 'Amsterdam', 'Netherlands', 'Weteringschans 6-8'),
('Palladium', 'Cologne', 'Germany', 'Schanzenstraße 36'),
('The Prospect Building', 'Bristol', 'UK', 'Feeder Road'),
('SWG3 Studio Warehouse', 'Glasgow', 'UK', '100 Eastvale Place'),
('O2 Academy Leeds', 'Leeds', 'UK', '55 Cookridge Street'),
('O2 Victoria Warehouse', 'Manchester', 'UK', 'Trafford Wharf Road'),
('Eventim Apollo', 'London', 'UK', '45 Queen Caroline Street'),
('Hard Rock Hotel Riviera Maya', 'Solidaridad', 'Mexico', 'Carretera Cancun-Tulum'),
('Holy Trinity Cathedral', 'Auckland', 'New Zealand', '446 Parnell Road'),
('Forum Melbourne', 'Melbourne', 'Australia', '154 Flinders Street'),
('Ancienne Belgique', 'Brussels', 'Belgium', 'Boulevard Anspach 110'),
('O2 Academy Birmingham', 'Birmingham', 'UK', '16–18 Horse Fair'),
('DEPOT', 'Cardiff', 'UK', '10–12 Dumballs Road');

INSERT INTO concerts (concert_date, venue_id, artist_id)
SELECT
    '2026-03-17',
    v.id,
    1
FROM venues v
WHERE v.venue_name = 'Paradiso';

INSERT INTO concerts (concert_date, venue_id, artist_id)
SELECT
    '2026-03-18',
    v.id,
    1
FROM venues v
WHERE v.venue_name = 'Palladium';

INSERT INTO concerts (concert_date, venue_id, artist_id)
SELECT
    '2026-03-20',
    v.id,
    1
FROM venues v
WHERE v.venue_name = 'The Prospect Building';

INSERT INTO concerts (concert_date, venue_id, artist_id)
SELECT
    '2026-03-21',
    v.id,
    1
FROM venues v
WHERE v.venue_name = 'SWG3 Studio Warehouse';

INSERT INTO concerts (concert_date, venue_id, artist_id)
SELECT
    '2026-03-22',
    v.id,
    1
FROM venues v
WHERE v.venue_name = 'O2 Academy Leeds';

INSERT INTO concerts (concert_date, venue_id, artist_id)
SELECT
    '2026-03-24',
    v.id,
    1
FROM venues v
WHERE v.venue_name = 'O2 Victoria Warehouse';

INSERT INTO concerts (concert_date, venue_id, artist_id)
SELECT
    '2026-03-25',
    v.id,
    1
FROM venues v
WHERE v.venue_name = 'Eventim Apollo';

INSERT INTO concerts (concert_date, venue_id, artist_id)
SELECT
    '2026-01-15',
    v.id,
    2
FROM venues v
WHERE v.venue_name = 'Hard Rock Hotel Riviera Maya';

INSERT INTO concerts (concert_date, venue_id, artist_id)
SELECT
    '2026-02-04',
    v.id,
    2
FROM venues v
WHERE v.venue_name = 'Holy Trinity Cathedral';

INSERT INTO concerts (concert_date, venue_id, artist_id)
SELECT
    '2026-02-09',
    v.id,
    2
FROM venues v
WHERE v.venue_name = 'Forum Melbourne';

INSERT INTO concerts (concert_date, venue_id, artist_id)
SELECT
    '2026-06-20',
    v.id,
    3
FROM venues v
WHERE v.venue_name = 'Ancienne Belgique';

INSERT INTO concerts (concert_date, venue_id, artist_id)
SELECT
    '2026-02-02',
    v.id,
    5
FROM venues v
WHERE v.venue_name = 'O2 Academy Birmingham';

INSERT INTO concerts (concert_date, venue_id, artist_id)
SELECT
    '2026-02-03',
    v.id,
    5
FROM venues v
WHERE v.venue_name = 'DEPOT';

