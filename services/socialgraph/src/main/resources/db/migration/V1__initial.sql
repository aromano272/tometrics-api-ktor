CREATE TABLE IF NOT EXISTS social_connections (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    connected_user_id VARCHAR(255) NOT NULL,
    connection_type VARCHAR(50) NOT NULL,
    created_at BIGINT NOT NULL,
    UNIQUE (user_id, connected_user_id, connection_type)
);

CREATE INDEX IF NOT EXISTS idx_social_connections_user_id ON social_connections (user_id);
CREATE INDEX IF NOT EXISTS idx_social_connections_connected_user_id ON social_connections (connected_user_id);