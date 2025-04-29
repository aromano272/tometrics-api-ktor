CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    idp_google_email TEXT UNIQUE,
    idp_facebook_id TEXT UNIQUE,
    idp_facebook_email TEXT,
    anon BOOLEAN NOT NULL
);

CREATE TABLE refresh_tokens (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id),
    token TEXT UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE plantings (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    plant_id INT NOT NULL,
    quantity INT NOT NULL,
    created_at TIMESTAMP DEFAULT now()
);
