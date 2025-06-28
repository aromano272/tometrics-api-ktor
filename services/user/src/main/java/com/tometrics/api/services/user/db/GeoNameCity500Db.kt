package com.tometrics.api.services.user.db

import com.tometrics.api.services.user.db.models.GeoNameCity500Entity
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery

@RegisterKotlinMapper(GeoNameCity500Entity::class)
interface GeoNameCity500Db {
    @SqlQuery("SELECT * FROM geoname_cities_500 ORDER BY name")
    fun getAll(): List<GeoNameCity500Entity>

    @SqlQuery(
        """
        SELECT *
        FROM geoname_cities_500
        WHERE geonameid = :id
        """
    )
    fun getById(@Bind("id") id: Int): GeoNameCity500Entity?

    @SqlQuery(
        """
        SELECT *
        FROM geoname_cities_500
        WHERE name ILIKE '%' || :name || '%' OR asciiname ILIKE '%' || :name || '%'
        ORDER BY population DESC
        """
    )
    fun getByName(@Bind("name") name: String): List<GeoNameCity500Entity>

    @SqlQuery(
        """
        SELECT *
        FROM geoname_cities_500
        WHERE country_code = :countryCode
        ORDER BY population DESC
        """
    )
    fun getByCountryCode(@Bind("countryCode") countryCode: String): List<GeoNameCity500Entity>

    @SqlQuery(
        """
        SELECT *,
          similarity(asciiname, unaccent(:name)) +
          CASE
            WHEN :asciiadmin1 IS NOT NULL AND asciiadmin1 % :asciiadmin1 THEN similarity(asciiadmin1, :asciiadmin1)
            ELSE 0.0
          END AS score
        FROM geoname_cities_500
        WHERE country_code = UPPER(:countryCode)
        ORDER BY score DESC
        LIMIT 1;
        """
    )
    fun findByNameAndAdmin1Similarity(
        @Bind("name") name: String,
        @Bind("countryCode") countryCode: String,
        @Bind("asciiadmin1") asciiadmin1: String?,
    ): GeoNameCity500Entity?

    @SqlQuery(
        """
        SELECT *
        FROM geoname_cities_500
        WHERE 
            asciiname ILIKE '%' || normalize(:query, NFD) || '%'
            OR country ILIKE '%' || normalize(:query, NFD) || '%'
            OR asciiadmin1 ILIKE '%' || normalize(:query, NFD) || '%'
            OR asciiadmin2 ILIKE '%' || normalize(:query, NFD) || '%'
        ORDER BY population DESC
        LIMIT 20
        """
    )
    fun search(@Bind("query") query: String): List<GeoNameCity500Entity>
}
