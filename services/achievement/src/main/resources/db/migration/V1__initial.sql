CREATE TABLE IF NOT EXISTS achievements
(
    user_id INT NOT NULL,
    type TEXT NOT NULL,
    count INT NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (user_id, type)
);

CREATE INDEX IF NOT EXISTS idx_achievements_user_id ON achievements (user_id);
