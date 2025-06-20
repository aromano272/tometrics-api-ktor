CREATE TABLE IF NOT EXISTS followers (
    user_id INT NOT NULL,
    followed_user_id INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (user_id, followed_user_id)
);

CREATE INDEX IF NOT EXISTS idx_social_connections_user_id ON followers (user_id);
CREATE INDEX IF NOT EXISTS idx_social_connections_followed_user_id ON followers (followed_user_id);
