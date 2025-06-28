CREATE TABLE users
(
    id                 SERIAL PRIMARY KEY,
    name               TEXT    NOT NULL,
    idp_google_email   TEXT UNIQUE,
    idp_facebook_id    TEXT UNIQUE,
    idp_facebook_email TEXT,
    anon               BOOLEAN NOT NULL,
    location_id        INT, -- REFERENCES geoname_cities_500 (geonameid) But i dont actually want to create the FK, cities might change
    metric_units       BOOLEAN DEFAULT TRUE,
    climate_zone       TEXT DEFAULT NULL,
    updated_at         TIMESTAMP DEFAULT now()
);

CREATE TABLE refresh_tokens
(
    id         SERIAL PRIMARY KEY,
    user_id    INT REFERENCES users (id),
    token      TEXT UNIQUE NOT NULL,
    expires_at TIMESTAMP   NOT NULL,
    created_at TIMESTAMP DEFAULT now()
);

CREATE TABLE geoname_cities_500
(
    geonameid                SERIAL PRIMARY KEY,  -- integer id of record in geonames database
    name              VARCHAR(200) NOT NULL, -- name of geographical point (utf8)
    asciiname         VARCHAR(200) NOT NULL, -- name of geographical point in plain ascii characters
    alternatenames    VARCHAR(10000), -- alternatenames, comma separated
    latitude          DOUBLE PRECISION NOT NULL, -- latitude in decimal degrees (wgs84)
    longitude         DOUBLE PRECISION NOT NULL, -- longitude in decimal degrees (wgs84)
    feature_class     CHAR(1) NOT NULL, -- see http://www.geonames.org/export/codes.html
    feature_code      VARCHAR(10) NOT NULL, -- see http://www.geonames.org/export/codes.html
    country           VARCHAR(80) NOT NULL, -- ISO-3166 2-letter country code
    country_code      CHAR(2) NOT NULL, -- ISO-3166 2-letter country code
    cc2               VARCHAR(200), -- alternate country codes, comma separated, ISO-3166 2-letter country code
    admin1            VARCHAR(200), -- first administrative division
    asciiadmin1       VARCHAR(200), -- first administrative division
    admin2            VARCHAR(200), -- second administrative division
    asciiadmin2       VARCHAR(200), -- second administrative division
    admin3_code       VARCHAR(20), -- code for third level administrative division
    admin4_code       VARCHAR(20), -- code for fourth level administrative division
    population        BIGINT NOT NULL, -- bigint (8 byte int)
    elevation         INTEGER, -- in meters, integer
    dem               INTEGER, -- digital elevation model, integer
    timezone          VARCHAR(40) NOT NULL, -- the iana timezone id
    modification_date VARCHAR(10) NOT NULL -- date of last modification in yyyy-MM-dd format
);

-- Create indexes for common queries
CREATE INDEX idx_geoname_cities_500_asciiname ON geoname_cities_500 (asciiname);
CREATE INDEX idx_geoname_cities_500_country ON geoname_cities_500 (country);
CREATE INDEX idx_geoname_cities_500_asciiadmin1 ON geoname_cities_500 (asciiadmin1);
CREATE INDEX idx_geoname_cities_500_asciiadmin2 ON geoname_cities_500 (asciiadmin2);
CREATE INDEX idx_geoname_cities_500_population ON geoname_cities_500 (population DESC);

CREATE EXTENSION pg_trgm;
CREATE EXTENSION unaccent;
