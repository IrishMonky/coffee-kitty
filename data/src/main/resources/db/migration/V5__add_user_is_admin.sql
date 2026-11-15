SET TIME ZONE 'UTC';

ALTER TABLE users
    ADD COLUMN is_admin boolean NOT NULL DEFAULT false;