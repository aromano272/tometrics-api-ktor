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
    country_code      CHAR(2) NOT NULL, -- ISO-3166 2-letter country code
    cc2               VARCHAR(200), -- alternate country codes, comma separated, ISO-3166 2-letter country code
    admin1_code       VARCHAR(20), -- fipscode (subject to change to iso code)
    admin2_code       VARCHAR(80), -- code for the second administrative division
    admin3_code       VARCHAR(20), -- code for third level administrative division
    admin4_code       VARCHAR(20), -- code for fourth level administrative division
    population        BIGINT NOT NULL, -- bigint (8 byte int)
    elevation         INTEGER, -- in meters, integer
    dem               INTEGER, -- digital elevation model, integer
    timezone          VARCHAR(40) NOT NULL, -- the iana timezone id
    modification_date VARCHAR(10) NOT NULL -- date of last modification in yyyy-MM-dd format
);

-- Create indexes for common queries
CREATE INDEX idx_geoname_cities_500_name ON geoname_cities_500 (name);
CREATE INDEX idx_geoname_cities_500_asciiname ON geoname_cities_500 (asciiname);
CREATE INDEX idx_geoname_cities_500_country_code ON geoname_cities_500 (country_code);
CREATE INDEX idx_geoname_cities_500_admin1_code ON geoname_cities_500 (admin1_code);
CREATE INDEX idx_geoname_cities_500_admin2_code ON geoname_cities_500 (admin2_code);
CREATE INDEX idx_geoname_cities_500_admin3_code ON geoname_cities_500 (admin3_code);
CREATE INDEX idx_geoname_cities_500_admin4_code ON geoname_cities_500 (admin4_code);
CREATE INDEX idx_geoname_cities_500_population ON geoname_cities_500 (population DESC);