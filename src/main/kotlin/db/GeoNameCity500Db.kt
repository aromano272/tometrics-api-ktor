package com.tometrics.api.db

import com.tometrics.api.db.models.GeoNameCity500Entity
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
        SELECT *
        FROM geoname_cities_500
        WHERE 
            name ILIKE '%' || :query || '%' 
            OR asciiname ILIKE '%' || :query || '%'
            OR country_code ILIKE '%' || :query || '%'
            OR admin1_code ILIKE '%' || :query || '%'
            OR admin2_code ILIKE '%' || :query || '%'
            OR admin3_code ILIKE '%' || :query || '%'
            OR admin4_code ILIKE '%' || :query || '%'
        ORDER BY population DESC
        LIMIT 20
        """
    )
    fun search(@Bind("query") query: String): List<GeoNameCity500Entity>
}
