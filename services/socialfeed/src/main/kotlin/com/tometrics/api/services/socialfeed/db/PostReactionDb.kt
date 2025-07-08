package com.tometrics.api.services.socialfeed.db

import com.tometrics.api.common.domain.models.PostId
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.socialfeed.db.models.PostReactionEntity
import com.tometrics.api.services.socialfeed.domain.models.Reaction
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindList
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jetbrains.annotations.Blocking
import java.time.Instant

@RegisterKotlinMapper(PostReactionEntity::class)
interface PostReactionDb {

    @Blocking
    @SqlUpdate("""
        INSERT INTO post_reactions (post_id, user_id, reaction)
        VALUES (:postId, :userId, :reaction)
    """)
    @GetGeneratedKeys
    fun insert(
        @Bind("postId") postId: PostId,
        @Bind("userId") userId: UserId,
        @Bind("reaction") reaction: Reaction,
    ): PostId?

    @Blocking
    @SqlUpdate("""
        UPDATE post_reactions 
        SET reaction = :reaction, 
        updated_at = NOW()
        WHERE id = :id AND user_id = :userId
    """)
    fun update(
        @Bind("postId") postId: PostId,
        @Bind("userId") userId: UserId,
        @Bind("reaction") reaction: Reaction,
    )

    @Blocking
    @SqlUpdate("DELETE FROM post_reactions WHERE post_id = :postId AND user_id = :userId")
    fun delete(
        @Bind("postId") postId: PostId,
        @Bind("userId") userId: UserId,
    )

    @Blocking
    @SqlQuery("""
        SELECT * FROM post_reactions
        WHERE post_id = :postId AND created_at < :olderThan
        ORDER BY created_at
        LIMIT :pageSize
    """)
    fun getAllByPostId(
        @Bind("postId") postId: PostId,
        @Bind("olderThan") olderThan: Instant,
        @Bind("pageSize") pageSize: Int,
    ): List<PostReactionEntity>

    @Blocking
    @SqlQuery("""
        SELECT * FROM post_reactions
        WHERE post_id IN (<postIds>)
        AND user_id = :userId
    """)
    fun getAllByPostIdsAndUserId(
        @BindList("postIds") postIds: Set<PostId>,
        @Bind("userId") userId: UserId,
    ): List<PostReactionEntity>

    @Blocking
    @SqlQuery("""
        SELECT DISTINCT ON (reaction) * FROM post_reactions
        WHERE post_id IN (<postIds>)
        LIMIT 3
    """)
    fun getLatestDistinctByPostId(
        @BindList("postIds") postIds: Set<PostId>,
    ): List<PostReactionEntity>

    @Blocking
    @SqlQuery("""
        SELECT * FROM post_reactions
        WHERE post_id = :postId
        AND user_id = :userId
    """)
    fun findByPostIdAndUserId(
        @Bind("postId") postId: PostId,
        @Bind("userId") userId: UserId,
    ): PostReactionEntity?

}