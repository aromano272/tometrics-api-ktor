package com.tometrics.api.services.socialfeed.db

import com.tometrics.api.common.domain.models.ImageUrl
import com.tometrics.api.common.domain.models.LocationInfoId
import com.tometrics.api.common.domain.models.PostId
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.socialfeed.db.models.PostEntity
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jetbrains.annotations.Blocking
import java.time.Instant

@RegisterKotlinMapper(PostEntity::class)
interface PostDb {

    @Blocking
    @SqlUpdate("""
        INSERT INTO posts (user_id, location_id, images, text)
        VALUES (:userId, :locationId, :images, :text)
    """)
    @GetGeneratedKeys
    fun insert(
        @Bind("userId") userId: UserId,
        @Bind("locationId") locationId: LocationInfoId?,
        @Bind("images") images: List<ImageUrl>,
        @Bind("text") text: String?,
    ): PostId?

    @Blocking
    @SqlUpdate("""
        UPDATE posts 
        SET location_id = COALESCE(:newLocationId, location_id), 
        images = COALESCE(:newImages, images), 
        text = COALESCE(:newText, text),
        WHERE id = :id AND user_id = :userId
    """)
    fun update(
        @Bind("id") id: PostId,
        @Bind("userId") userId: UserId,
        @Bind("newLocationId") newLocationId: LocationInfoId?,
        @Bind("newImages") newImages: List<ImageUrl>?,
        @Bind("newText") newText: String?,
    )

    @Blocking
    @SqlUpdate("DELETE FROM posts WHERE id = :id AND user_id = :userId")
    fun delete(
        @Bind("id") id: PostId,
        @Bind("userId") userId: UserId,
    )

    // NOTE(aromano): at the moment the feed is going to be all posts from everyone, for simplicity and to drive engagement
    @Blocking
    @SqlQuery("""
        SELECT * FROM posts
        WHERE created_at < :olderThan
        ORDER BY created_at
        LIMIT :pageSize
    """)
    fun getFeed(
        @Bind("olderThan") olderThan: Instant,
        @Bind("pageSize") pageSize: Int,
    ): List<PostEntity>

    @Blocking
    @SqlQuery("""
        SELECT * FROM posts
        WHERE user_id = :userId AND created_at < :olderThan
        ORDER BY created_at
        LIMIT :pageSize
    """)
    fun getAllByUserId(
        @Bind("userId") userId: UserId,
        @Bind("olderThan") olderThan: Instant,
        @Bind("pageSize") pageSize: Int,
    ): List<PostEntity>

    @Blocking
    @SqlQuery("""
        SELECT * FROM posts
        WHERE id = :postId
    """)
    fun findById(@Bind("id") id: PostId): PostEntity?

    @Blocking
    @SqlUpdate("UPDATE posts SET reaction_count = reaction_count + 1 WHERE id = :id")
    fun increaseReactionCount(@Bind("id") id: PostId)

    @Blocking
    @SqlUpdate("UPDATE posts SET reaction_count = reaction_count - 1 WHERE id = :id")
    fun decreaseReactionCount(@Bind("id") id: PostId)

}