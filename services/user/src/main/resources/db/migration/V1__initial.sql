CREATE TABLE users
(
    id                 SERIAL PRIMARY KEY,
    name               TEXT    NOT NULL,
    idp_google_email   TEXT UNIQUE,
    idp_facebook_id    TEXT UNIQUE,
    idp_facebook_email TEXT,
    anon               BOOLEAN NOT NULL
);

