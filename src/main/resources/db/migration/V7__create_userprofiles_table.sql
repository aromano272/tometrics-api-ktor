CREATE TABLE userprofiles
(
    user_id            INT PRIMARY KEY REFERENCES users (id) ON DELETE CASCADE,
    name               TEXT,
    location_id        INT, -- REFERENCES geoname_cities_500 (geonameid) But i dont actually want to create the FK, cities might change
    updated_at         TIMESTAMP DEFAULT now()
);