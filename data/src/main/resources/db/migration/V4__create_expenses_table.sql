SET TIME ZONE 'UTC';

CREATE TABLE expenses (
    id uuid NOT NULL PRIMARY KEY,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    user_id uuid NOT NULL REFERENCES users(id),
    bean_name varchar(255) NOT NULL CHECK (char_length(trim(bean_name)) > 0),
    weight_grams int NOT NULL CHECK (weight_grams BETWEEN 100 AND 1000),
    amount numeric(10, 2) NOT NULL CHECK (amount BETWEEN 0.01 AND 1000.00),
    note varchar(500)
);
