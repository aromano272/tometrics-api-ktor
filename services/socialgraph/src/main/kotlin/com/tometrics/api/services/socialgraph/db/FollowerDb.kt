package com.tometrics.api.services.socialgraph.db

import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.socialgraph.db.models.FollowerEntity
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

@RegisterKotlinMapper(FollowerEntity::class)
interface FollowerDb {

    @SqlUpdate("""
        INSERT INTO followers (user_id, followed_user_id)
        VALUES (:userId, :followedUserId)
    """)
    @GetGeneratedKeys
    fun insert(
        @Bind("userId") userId: UserId,
        @Bind("followedUserId") followedUserId: UserId,
    ): Int

    @SqlQuery("DELETE FROM followers WHERE user_id = :userId AND followed_user_id = :followedUserId")
    fun delete(
        @Bind("userId") userId: UserId,
        @Bind("followedUserId") followedUserId: UserId,
    ): Int

    @SqlQuery("SELECT * FROM followers WHERE user_id = :userId")
    fun getAllFollowedByUserId(@Bind("userId") userId: UserId): List<FollowerEntity>

    @SqlQuery("SELECT * FROM followers WHERE followed_user_id = :userId")
    fun getAllFollowersOfUserId(@Bind("userId") userId: UserId): List<FollowerEntity>

}