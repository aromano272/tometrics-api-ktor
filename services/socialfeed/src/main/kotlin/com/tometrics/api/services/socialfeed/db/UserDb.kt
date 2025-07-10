package com.tometrics.api.services.socialfeed.db

import com.tometrics.api.common.domain.models.ClimateZone
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.socialfeed.db.models.UserEntity
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindList
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jetbrains.annotations.Blocking

@RegisterKotlinMapper(UserEntity::class)
interface UserDb {

    @Blocking
    @SqlUpdate("""
        INSERT INTO users (id, name, climate_zone)
        VALUES (:id, :name, :climateZone)
    """)
    @GetGeneratedKeys
    fun insert(
        @Bind("id") id: UserId,
        @Bind("name") name: String,
        @Bind("climateZone") climateZone: ClimateZone?,
    ): UserId?

    @Blocking
    @SqlUpdate("""
        UPDATE users 
        SET name = COALESCE(:newName, name), 
        climate_zone = COALESCE(:newClimateZone, climate_zone),
        updated_at = NOW()
        WHERE id = :id
    """)
    fun update(
        @Bind("id") id: UserId,
        @Bind("name") name: String?,
        @Bind("climateZone") climateZone: ClimateZone?,
    )

    @Blocking
    @SqlUpdate("DELETE FROM users WHERE id = :userId")
    fun delete(
        @Bind("id") id: UserId,
    )

    @Blocking
    @SqlQuery("SELECT * FROM users WHERE id = :id")
    fun findById(@Bind("id") id: UserId): UserEntity?

    @Blocking
    @SqlQuery("SELECT * FROM users WHERE id = ANY(:ids)")
    fun getAllByIds(@Bind("ids") ids: Set<UserId>): List<UserEntity>

}