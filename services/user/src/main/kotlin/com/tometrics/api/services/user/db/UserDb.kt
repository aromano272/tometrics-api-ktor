package com.tometrics.api.services.user.db

import com.tometrics.api.common.domain.models.LocationInfoId
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.user.db.models.UserEntity
import com.tometrics.api.services.user.domain.models.ClimateZone
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jetbrains.annotations.Blocking

@RegisterKotlinMapper(UserEntity::class)
interface UserDb {

    @Blocking
    @SqlUpdate(
        """
            INSERT INTO users (
                name, 
                idp_google_email, 
                idp_facebook_id, 
                idp_facebook_email, 
                anon,
                location_id,
                metric_units,
                climate_zone,
                updated_at
            ) 
            VALUES (
                :name, 
                :idpGoogleEmail, 
                :idpFacebookId, 
                :idpFacebookEmail, 
                :anon,
                :locationId,
                :metricUnits,
                :climateZone,
                NOW()
            )
        """
    )
    @GetGeneratedKeys
    fun insert(
        @Bind("name") name: String,
        @Bind("idpGoogleEmail") idpGoogleEmail: String?,
        @Bind("idpFacebookId") idpFacebookId: String?,
        @Bind("idpFacebookEmail") idpFacebookEmail: String?,
        @Bind("anon") anon: Boolean,
        @Bind("locationId") locationId: LocationInfoId?,
        @Bind("metricUnits") metricUnits: Boolean,
        @Bind("climateZone") climateZone: ClimateZone?,
    ): Int

    @Blocking
    @SqlUpdate(
        """
            UPDATE users SET
            idp_google_email = COALESCE(:idpGoogleEmail, idp_google_email),
            idp_facebook_id = COALESCE(:idpFacebookId, idp_facebook_id),
            idp_facebook_email = COALESCE(:idpFacebookEmail, idp_facebook_email),
            anon = :anon,
            name = COALESCE(:name, name),
            location_id = COALESCE(:locationId, location_id),
            metric_units = COALESCE(:metricUnits, metric_units),
            climate_zone = COALESCE(:climateZone, climate_zone),
            updated_at = NOW()
            WHERE id = :id
            """
    )
    fun update(
        @Bind("id") id: UserId,
        @Bind("name") name: String?,
        @Bind("idpGoogleEmail") idpGoogleEmail: String?,
        @Bind("idpFacebookId") idpFacebookId: String?,
        @Bind("idpFacebookEmail") idpFacebookEmail: String?,
        @Bind("anon") anon: Boolean?,
        @Bind("locationId") locationId: LocationInfoId?,
        @Bind("metricUnits") metricUnits: Boolean?,
        @Bind("climateZone") climateZone: ClimateZone?,
    ): Int

    @Blocking
    @SqlQuery("SELECT * FROM users WHERE id = ANY(:ids)")
    fun getAllByIds(@Bind("ids") ids: Set<Int>): List<UserEntity>

    @Blocking
    @SqlQuery("SELECT * FROM users WHERE idp_google_email = :idpGoogleEmail")
    fun findByGoogleEmail(@Bind("idpGoogleEmail") idpGoogleEmail: String): UserEntity?

    @Blocking
    @SqlQuery("SELECT * FROM users WHERE idp_facebook_id = :idpFacebookId")
    fun findByFacebookId(@Bind("idpFacebookId") idpFacebookId: String): UserEntity?

    @Blocking
    @SqlQuery("SELECT * FROM users WHERE id = :id")
    fun findById(@Bind("id") id: Int): UserEntity?
}
