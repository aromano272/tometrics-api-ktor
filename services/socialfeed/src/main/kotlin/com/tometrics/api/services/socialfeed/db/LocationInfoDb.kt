package com.tometrics.api.services.socialfeed.db

import com.tometrics.api.common.domain.models.LocationInfoId
import com.tometrics.api.services.socialfeed.db.models.LocationInfoEntity
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jetbrains.annotations.Blocking

@RegisterKotlinMapper(LocationInfoEntity::class)
interface LocationInfoDb {

    @Blocking
    @SqlUpdate("""
        INSERT INTO location_info (location_id, city, country, country_code)
        VALUES (:locationId, :city, :country, :countryCode)
    """)
    @GetGeneratedKeys
    fun insert(
        @Bind("locationId") locationId: LocationInfoId,
        @Bind("city") city: String?,
        @Bind("country") country: String?,
        @Bind("countryCode") countryCode: String?,
    ): LocationInfoId?

    @Blocking
    @SqlUpdate("""
        UPDATE location_info 
        SET city = COALESCE(:newCity, city), 
        country = COALESCE(:newCountry, country),
        country_code = COALESCE(:newCountryCode, country_code),
        updated_at = NOW()
        WHERE id = :id
    """)
    fun update(
        @Bind("locationId") locationId: LocationInfoId,
        @Bind("city") city: String?,
        @Bind("country") country: String?,
        @Bind("countryCode") countryCode: String?,
    )

    @Blocking
    @SqlUpdate("DELETE FROM location_info WHERE location_id = :locationId")
    fun delete(
        @Bind("locationId") locationId: LocationInfoId,
    )

}