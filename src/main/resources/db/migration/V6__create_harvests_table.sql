CREATE TABLE harvests
(
    id                 SERIAL PRIMARY KEY,
    planting_id        INT REFERENCES plantings (id) ON DELETE CASCADE,
    quantity           FLOAT  NOT NULL,
    unit               TEXT NOT NULL,
    created_at         TIMESTAMP DEFAULT now()
);