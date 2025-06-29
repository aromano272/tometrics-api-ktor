CREATE TABLE plants
(
    id                   SERIAL PRIMARY KEY,
    name                 TEXT    NOT NULL,
    time_to_harvest      INTEGER NOT NULL,
    yield_per_plant_from FLOAT   NOT NULL,
    yield_per_plant_to   FLOAT   NOT NULL,
    yield_per_plant_unit TEXT    NOT NULL,
    yield_per_sqm_from   FLOAT   NOT NULL,
    yield_per_sqm_to     FLOAT   NOT NULL,
    yield_per_sqm_unit   TEXT    NOT NULL,
    companion_plants     TEXT[] NOT NULL,
    climate_zones        JSONB   NOT NULL,
    spacing              TEXT    NOT NULL,
    sunlight             TEXT    NOT NULL,
    daily_sunlight       TEXT    NOT NULL,
    soil_types           TEXT[] NOT NULL,
    water_requirement    TEXT    NOT NULL,
    growth_habit         TEXT    NOT NULL,
    growing_tips         TEXT[] NOT NULL
);

CREATE TABLE plantings
(
    id                  SERIAL PRIMARY KEY,
    user_id             INT REFERENCES users (id) ON DELETE CASCADE,
    plant_id            INT REFERENCES plants (id) ON DELETE CASCADE,
    quantity            INT       NOT NULL,
    name                TEXT,
    diary               TEXT NOT NULL DEFAULT '',
    harvested           BOOLEAN NOT NULL DEFAULT false,
    created_at          TIMESTAMP DEFAULT now(),
    ready_to_harvest_at TIMESTAMP NOT NULL
);

CREATE TABLE harvests
(
    id                 SERIAL PRIMARY KEY,
    planting_id        INT REFERENCES plantings (id) ON DELETE CASCADE,
    quantity           FLOAT  NOT NULL,
    unit               TEXT NOT NULL,
    created_at         TIMESTAMP DEFAULT now()
);
