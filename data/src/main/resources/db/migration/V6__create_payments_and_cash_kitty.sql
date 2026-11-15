SET TIME ZONE 'UTC';

CREATE TABLE cash_kitty (
    id uuid NOT NULL PRIMARY KEY,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    total_balance numeric(10, 2) NOT NULL DEFAULT 0.00
);

INSERT INTO cash_kitty (id, created_at, updated_at, total_balance)
VALUES ('00000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP AT TIME ZONE 'UTC', CURRENT_TIMESTAMP AT TIME ZONE 'UTC', 0.00);

CREATE TABLE payments (
    id uuid NOT NULL PRIMARY KEY,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    user_id uuid NOT NULL REFERENCES users(id),
    recorded_by_user_id uuid NOT NULL REFERENCES users(id),
    amount numeric(10, 2) NOT NULL CHECK (amount BETWEEN 0.01 AND 1000.00)
);