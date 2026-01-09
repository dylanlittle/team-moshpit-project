BEGIN;

WITH inserted_users AS (
  INSERT INTO users (name, username, email, bio, location)
  VALUES
    ('Dylan Little','dylanlittle','dylan.little@example.com','Building Moshpit and living in gig listings.','London, UK'),
    ('Francesca Russo','franrusso','francesca.russo@example.com','Indie/alt obsessive. Always chasing the next show.','Manchester, UK'),
    ('Alex Turner','alexturner2','alex.turner2@example.com','Basslines > everything.','Leeds, UK'),
    ('Samira Khan','samirak','samira.khan@example.com','Metal and synthwave. No skips.','Birmingham, UK'),
    ('Jamie O''Connor','jamieoc','jamie.oconnor@example.com','Shoegaze head. Pedalboard nerd.','Bristol, UK'),
    ('Priya Desai','priyad','priya.desai@example.com','Festival planner. Spreadsheet enjoyer.','Edinburgh, UK'),
    ('Tom Bennett','tomben','tom.bennett@example.com','I go where the drums go.','Glasgow, UK'),
    ('Mia Carter','miacarter','mia.carter@example.com','Live music photographer (mostly blurry, all vibes).','Brighton, UK')
  RETURNING id, username
),
inserted_artists AS (
  INSERT INTO artists (name, bio, genre)
  VALUES
    ('Radiohead','English rock band known for their experimental approach, emotionally charged songwriting, and constant reinvention across alternative and electronic music.','Alternative Rock'),
    ('Phoebe Bridgers','American singer-songwriter acclaimed for her intimate lyrics, hushed vocals, and emotionally raw indie-folk sound.','Indie Folk'),
    ('Arctic Monkeys','Sheffield-formed band blending sharp lyricism with evolving styles ranging from garage rock to lounge-inflected art rock.','Indie Rock'),
    ('The 1975','Manchester-based pop-rock band mixing glossy production with introspective, self-aware songwriting and genre-blurring influences.','Pop Rock'),
    ('Fontaines D.C.','Irish post-punk band celebrated for poetic lyricism, gritty guitar work, and a modern take on classic punk aesthetics.','Post-punk'),
    ('Tame Impala','Psychedelic music project led by Kevin Parker, fusing dreamy synths, groove-heavy basslines, and introspective pop songwriting.','Psychedelic Pop'),
    ('Bring Me The Horizon','British band that evolved from metalcore roots into a boundary-pushing blend of metal, electronic, and alternative rock.','Metal'),
    ('Lana Del Rey','Singer-songwriter known for cinematic pop music, nostalgic Americana themes, and melancholic, atmospheric production.','Dream Pop'),
    ('Four Tet','Electronic producer blending ambient textures, organic rhythms, and club-focused experimentation.','Electronic'),
    ('Wolf Alice','London-based band combining grunge, dream pop, and indie rock with powerful vocals and dynamic songwriting.','Alternative Rock')
  RETURNING id, name
),
inserted_venues AS (
  INSERT INTO venues (venue_name, city, country, address)
  VALUES
    ('O2 Academy Brixton', 'London',     'UK', '211 Stockwell Rd, London SW9 9SL'),
    ('Manchester Academy', 'Manchester', 'UK', 'Oxford Rd, Manchester M13 9PR'),
    ('O2 Academy Glasgow', 'Glasgow',    'UK', '121 Eglinton St, Glasgow G5 9NT'),
    ('Thekla',             'Bristol',    'UK', 'The Grove, East Mud Dock, Bristol BS1 4RB'),
    ('Rock City',          'Nottingham', 'UK', '8 Talbot St, Nottingham NG1 5GG')
  RETURNING id, venue_name
),
inserted_concerts AS (
  INSERT INTO concerts (concert_date, venue_id, artist_id)
  SELECT v.concert_date, iv.id, ia.id
  FROM (
    VALUES
      (DATE '2026-02-01', 'O2 Academy Brixton', 'Radiohead'),
      (DATE '2026-02-08', 'Manchester Academy', 'The 1975'),
      (DATE '2026-02-14', 'O2 Academy Glasgow', 'Bring Me The Horizon'),
      (DATE '2026-03-02', 'Thekla', 'Phoebe Bridgers'),
      (DATE '2026-03-15', 'Rock City', 'Tame Impala'),
      (DATE '2026-03-28', 'O2 Academy Brixton', 'Lana Del Rey'),
      (DATE '2026-04-05', 'Manchester Academy', 'Arctic Monkeys')
  ) AS v(concert_date, venue_name, artist_name)
  JOIN inserted_venues  iv ON iv.venue_name = v.venue_name
  JOIN inserted_artists ia ON ia.name       = v.artist_name
  RETURNING id, concert_date
),
inserted_lineups AS (
  INSERT INTO lineup_artists (artist_id, concert_id)
  SELECT ia.id, ic.id
  FROM (
    VALUES
      (DATE '2026-02-01', 'Radiohead'),
      (DATE '2026-02-01', 'Four Tet'),
      (DATE '2026-02-01', 'Wolf Alice'),

      (DATE '2026-02-08', 'The 1975'),
      (DATE '2026-02-08', 'Phoebe Bridgers'),
      (DATE '2026-02-08', 'Fontaines D.C.'),

      (DATE '2026-02-14', 'Bring Me The Horizon'),
      (DATE '2026-02-14', 'Arctic Monkeys'),
      (DATE '2026-02-14', 'Wolf Alice'),

      (DATE '2026-03-02', 'Phoebe Bridgers'),
      (DATE '2026-03-02', 'Lana Del Rey'),
      (DATE '2026-03-02', 'Four Tet'),

      (DATE '2026-03-15', 'Tame Impala'),
      (DATE '2026-03-15', 'The 1975'),
      (DATE '2026-03-15', 'Lana Del Rey'),

      (DATE '2026-03-28', 'Lana Del Rey'),
      (DATE '2026-03-28', 'Four Tet'),
      (DATE '2026-03-28', 'Fontaines D.C.'),

      (DATE '2026-04-05', 'Arctic Monkeys'),
      (DATE '2026-04-05', 'Wolf Alice'),
      (DATE '2026-04-05', 'Fontaines D.C.')
  ) AS v(concert_date, artist_name)
  JOIN inserted_concerts ic ON ic.concert_date = v.concert_date
  JOIN inserted_artists  ia ON ia.name         = v.artist_name
  RETURNING concert_id, artist_id
),
inserted_goers AS (
  INSERT INTO concert_goers (user_id, concert_id)
  SELECT iu.id, ic.id
  FROM (
    VALUES
      ('dylanlittle', DATE '2026-02-01'),
      ('dylanlittle', DATE '2026-02-08'),
      ('franrusso',   DATE '2026-02-08'),
      ('alexturner2', DATE '2026-02-01'),
      ('alexturner2', DATE '2026-02-14'),
      ('samirak',     DATE '2026-02-14'),
      ('jamieoc',     DATE '2026-03-02'),
      ('priyad',      DATE '2026-03-15'),
      ('tomben',      DATE '2026-03-28'),
      ('miacarter',   DATE '2026-02-08'),
      ('miacarter',   DATE '2026-03-28')
  ) AS v(username, concert_date)
  JOIN inserted_users    iu ON iu.username     = v.username
  JOIN inserted_concerts ic ON ic.concert_date = v.concert_date
  RETURNING user_id, concert_id
),

inserted_artist_posts AS (
  INSERT INTO posts (content, artist_id)
  SELECT v.content, ia.id
  FROM (
    VALUES
      ('Back in the rehearsal room. Old songs, new shapes.', 'Radiohead'),
      ('Soundcheck experiments getting strange again.',      'Radiohead'),
      ('Brixton — see you soon.',                            'Radiohead'),
      ('Thank you for listening so closely last night.',     'Radiohead'),

      ('Wrote something sad. Shocking, I know.',             'Phoebe Bridgers'),
      ('Tour bus thoughts at 2am.',                          'Phoebe Bridgers'),
      ('Trying a quieter arrangement for this next run.',    'Phoebe Bridgers'),
      ('Thank you for being so gentle with these songs.',    'Phoebe Bridgers'),

      ('New riffs knocking about.',                          'Arctic Monkeys'),
      ('Back on stage — felt right.',                        'Arctic Monkeys'),
      ('Manchester always delivers.',                        'Arctic Monkeys'),
      ('More dates incoming.',                               'Arctic Monkeys'),

      ('Soundcheck turned into a jam session.',              'The 1975'),
      ('Trying something new tonight.',                      'The 1975'),
      ('Thank you for singing louder than us.',              'The 1975'),
      ('Tour diary entry #7.',                               'The 1975'),

      ('Back in the practice room. Sweat and poetry.',       'Fontaines D.C.'),
      ('New material taking shape.',                         'Fontaines D.C.'),
      ('That crowd energy was unreal.',                      'Fontaines D.C.'),
      ('See you down the front.',                            'Fontaines D.C.'),

      ('Synths everywhere again.',                           'Tame Impala'),
      ('Studio lights on, world off.',                       'Tame Impala'),
      ('Trying to make something that feels like summer.',   'Tame Impala'),
      ('Live version hitting different.',                    'Tame Impala'),

      ('Louder. Always louder.',                             'Bring Me The Horizon'),
      ('New set feels dangerous in the best way.',           'Bring Me The Horizon'),
      ('Glasgow nearly broke the floor.',                    'Bring Me The Horizon'),
      ('More chaos coming.',                                 'Bring Me The Horizon'),

      ('Wrote something by the piano tonight.',              'Lana Del Rey'),
      ('Dreaming up a new live atmosphere.',                 'Lana Del Rey'),
      ('London felt cinematic.',                             'Lana Del Rey'),
      ('Thank you for listening so quietly.',                'Lana Del Rey'),

      ('Testing rhythms for the next live set.',             'Four Tet'),
      ('Late night ideas turning into something real.',      'Four Tet'),
      ('Really enjoyed stretching things out last night.',   'Four Tet'),
      ('New textures coming soon.',                          'Four Tet'),

      ('Back together in the room again.',                   'Wolf Alice'),
      ('Trying heavier tones for this run.',                 'Wolf Alice'),
      ('Crowds have been unreal lately.',                    'Wolf Alice'),
      ('See you right at the barrier.',                      'Wolf Alice')
  ) AS v(content, artist_name)
  JOIN inserted_artists ia ON ia.name = v.artist_name
  RETURNING id
),

inserted_user_concert_posts AS (
  INSERT INTO posts (content, user_id, concert_id)
  SELECT v.content, iu.id, ic.id
  FROM (
    VALUES
      ('Anyone know set times for tonight?',                          'dylanlittle', DATE '2026-02-01'),
      ('Queue looking spicy already 😅',                              'alexturner2', DATE '2026-02-01'),
      ('If you see a lost earplug case, it''s mine.',                 'miacarter',   DATE '2026-02-01'),
      ('Soundcheck leak from outside was insane.',                    'jamieoc',     DATE '2026-02-01'),
      ('Who''s heading for merch first?',                             'franrusso',   DATE '2026-02-01'),

      ('Train delays… praying I make doors.',                         'dylanlittle', DATE '2026-02-08'),
      ('Anyone want to swap balcony for standing?',                   'franrusso',   DATE '2026-02-08'),
      ('I''m here solo — say hi if you spot me!',                     'miacarter',   DATE '2026-02-08'),
      ('Support act just started, vibes are great.',                  'priyad',      DATE '2026-02-08'),
      ('Manchester crowd always goes hard.',                          'alexturner2', DATE '2026-02-08'),
      ('If you hear someone scream every lyric, that''s me.',         'samirak',     DATE '2026-02-08'),

      ('Happy Valentine’s Day: I chose distortion.',                  'samirak',     DATE '2026-02-14'),
      ('If anyone has a spare ticket, mate got scammed.',             'tomben',      DATE '2026-02-14'),
      ('Earplugs ON. Let’s go.',                                      'alexturner2', DATE '2026-02-14'),
      ('Anyone know if coats are £3 or £4 tonight?',                  'dylanlittle', DATE '2026-02-14'),
      ('Glasgow crowd is unreal already.',                            'miacarter',   DATE '2026-02-14'),

      ('Thekla is such a cool venue. First time here!',               'jamieoc',     DATE '2026-03-02'),
      ('If you''re near the front, pls leave breathing room 😭',      'franrusso',   DATE '2026-03-02'),
      ('Anyone got a lighter? (for candles, obviously)',              'dylanlittle', DATE '2026-03-02'),
      ('Sound is *perfect* where I''m stood.',                        'priyad',      DATE '2026-03-02'),
      ('Post-show drinks nearby?',                                    'alexturner2', DATE '2026-03-02'),

      ('Rock City is packedddd.',                                     'priyad',      DATE '2026-03-15'),
      ('If you''re driving back to Derby, I''ll chip in petrol.',     'tomben',      DATE '2026-03-15'),
      ('That bass just rearranged my organs.',                        'alexturner2', DATE '2026-03-15'),
      ('Balcony seats are elite tbh.',                                'miacarter',   DATE '2026-03-15'),
      ('Setlist predictions? I''m hoping for deep cuts.',             'dylanlittle', DATE '2026-03-15'),
      ('Merch queue update: it''s… not moving.',                      'jamieoc',     DATE '2026-03-15'),

      ('Brixton again! Best venue in London and it''s not close.',    'dylanlittle', DATE '2026-03-28'),
      ('If you''ve got spare earplugs, I''ll buy you a drink.',       'franrusso',   DATE '2026-03-28'),
      ('Crowd is super respectful tonight — love it.',                'priyad',      DATE '2026-03-28'),
      ('Anyone else freezing outside?',                               'tomben',      DATE '2026-03-28'),
      ('The lighting is insane. Photos incoming.',                    'miacarter',   DATE '2026-03-28'),

      ('Manchester round 2. Let''s make it loud.',                    'alexturner2', DATE '2026-04-05'),
      ('Got last minute tickets — buzzing.',                          'dylanlittle', DATE '2026-04-05'),
      ('Anyone know if there''s a curfew tonight?',                   'priyad',      DATE '2026-04-05'),
      ('Soundcheck teased my favourite track 😭',                      'franrusso',   DATE '2026-04-05'),
      ('If you see a black beanie on the floor, it''s mine.',         'tomben',      DATE '2026-04-05'),
      ('Post-show: meet by the front doors?',                         'miacarter',   DATE '2026-04-05'),

      ('Can someone confirm doors time? My mate is arguing with me.', 'jamieoc',     DATE '2026-02-08'),
      ('Hydrate + earplugs = survive the pit.',                       'samirak',     DATE '2026-02-14'),
      ('If you''re tall, pls don''t lock your elbows in front of me 😅','franrusso',  DATE '2026-03-28'),
      ('That opener absolutely smashed it.',                          'tomben',      DATE '2026-03-02'),
      ('First gig of the year — missed this so much.',                'priyad',      DATE '2026-02-01')
  ) AS v(content, username, concert_date)
  JOIN inserted_users    iu ON iu.username     = v.username
  JOIN inserted_concerts ic ON ic.concert_date = v.concert_date
  RETURNING id
)

SELECT 1;

COMMIT;
