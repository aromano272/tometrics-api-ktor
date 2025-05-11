package com.tometrics.api.db.db

import com.tometrics.api.db.models.LocationInfoId
import com.tometrics.api.db.models.UserProfileEntity
import com.tometrics.api.domain.models.UserId
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

@RegisterKotlinMapper(UserProfileEntity::class)
interface UserProfileDb {

    @SqlQuery(
        """
        SELECT * FROM userprofiles
        WHERE user_id = :userId
    """
    )
    fun findById(@Bind("userId") userId: UserId): UserProfileEntity?

    @SqlUpdate(
        """
            INSERT INTO userprofiles (user_id, name, location_id, updated_at)
            VALUES (:userId, :name, :locationId, NOW())
            ON CONFLICT (user_id) DO UPDATE
            SET name = :name, location_id = :location_id, updated_at = NOW()
        """
    )
    @GetGeneratedKeys
    fun upsert(
        @Bind("userId") userId: UserId,
        @Bind("name") name: String?,
        @Bind("locationId") locationId: LocationInfoId?,
    ): UserId

}