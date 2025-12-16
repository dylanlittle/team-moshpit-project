CREATE TABLE artists (
                          id bigserial PRIMARY KEY,
                          name varchar(50) NOT NULL,
                          bio text NOT NULL,
                          genre varchar(50) NOT NULL
);

CREATE TABLE posts(
                        id bigserial PRIMARY KEY,
                        content TEXT NOT NULL,
                        timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
