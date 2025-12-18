ALTER table posts ADD COLUMN user_id BIGINT,
    ADD CONSTRAINT fk_user
    FOREIGN KEY (user_id)
    REFERENCES users (id)
    ON DELETE CASCADE;