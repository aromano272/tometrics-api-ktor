CREATE TABLE IF NOT EXISTS posts
(
    id  INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users (user_id) ON DELETE CASCADE,
    images TEXT[] NOT NULL DEFAULT array[]::text[],
    text TEXT NOT NULL,
    reactions_count INT NOT NULL DEFAULT 0,
    comments_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS users
(
    id INT PRIMARY KEY,
    name TEXT,
    location_id INT REFERENCES location_info (location_id),
    climate_zone TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS location_info
(
    location_id INT PRIMARY KEY,
    city TEXT,
    country TEXT,
    country_code TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS post_reactions
(
    post_id INT NOT NULL REFERENCES posts (id) ON DELETE CASCADE,
    user_id INT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    reaction TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
    UNIQUE(post_id, user_id)
);

CREATE TABLE IF NOT EXISTS comments (
    id  INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    parent_id INT REFERENCES comments (id) ON DELETE CASCADE,
    text TEXT NOT NULL,
    image TEXT,
    reactions_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS comment_reactions
(
    comment_id INT NOT NULL REFERENCES comments (id) ON DELETE CASCADE,
    user_id INT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    reaction TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
    UNIQUE(comment_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_posts_user_id ON posts (user_id);
