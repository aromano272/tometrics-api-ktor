package com.tometrics.api.services.socialfeed.db

import com.tometrics.api.common.domain.models.CommentId
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.socialfeed.db.models.CommentReactionEntity
import com.tometrics.api.services.socialfeed.domain.models.Reaction
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jetbrains.annotations.Blocking
import java.time.Instant

@RegisterKotlinMapper(CommentReactionEntity::class)
interface CommentReactionDb {

    @Blocking
    @SqlUpdate("""
        INSERT INTO comment_reactions (comment_id, user_id, reaction)
        VALUES (:commentId, :userId, :reaction)
    """)
    @GetGeneratedKeys
    fun insert(
        @Bind("commentId") commentId: CommentId,
        @Bind("userId") userId: UserId,
        @Bind("reaction") reaction: Reaction,
    ): CommentId?

    @Blocking
    @SqlUpdate("""
        UPDATE comment_reactions 
        SET reaction = :reaction, 
        updated_at = NOW()
        WHERE id = :id AND user_id = :userId
    """)
    fun update(
        @Bind("commentId") commentId: CommentId,
        @Bind("userId") userId: UserId,
        @Bind("reaction") reaction: Reaction,
    )

    @Blocking
    @SqlUpdate("DELETE FROM comment_reactions WHERE comment_id = :commentId AND user_id = :userId")
    fun delete(
        @Bind("commentId") commentId: CommentId,
        @Bind("userId") userId: UserId,
    )

    @Blocking
    @SqlQuery("""
        SELECT * FROM comment_reactions
        WHERE comment_id = :commentId AND created_at < :olderThan
        ORDER BY created_at
        LIMIT :pageSize
    """)
    fun getAllByCommentId(
        @Bind("commentId") commentId: CommentId,
        @Bind("olderThan") olderThan: Instant,
        @Bind("pageSize") pageSize: Int,
    ): List<CommentReactionEntity>

    @Blocking
    @SqlQuery("""
        SELECT * FROM comment_reactions
        WHERE comment_id = :commentId
        AND user_id = :userId
    """)
    fun findByCommentIdAndUserId(commentId: CommentId, userId: UserId): CommentReactionEntity?

}