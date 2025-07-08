package com.tometrics.api.services.socialfeed.db

import com.tometrics.api.common.domain.models.CommentId
import com.tometrics.api.common.domain.models.PostId
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.socialfeed.db.models.CommentEntity
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jetbrains.annotations.Blocking
import java.time.Instant


@RegisterKotlinMapper(CommentEntity::class)
interface CommentDb {

    @Blocking
    @SqlUpdate("""
        INSERT INTO comments (user_id, parent_id, text, image)
        VALUES (:userId, :parentId, text, image)
    """)
    @GetGeneratedKeys
    fun insert(
        @Bind("userId") userId: UserId,
        @Bind("parentId") parentId: CommentId?,
        @Bind("text") text: String,
        @Bind("image") image: String?,
    ): CommentId?

    @Blocking
    @SqlUpdate("""
        UPDATE comments 
        SET text = COALESCE(:newText, text), 
        image = :newImage,
        updated_at = NOW()
        WHERE id = :id AND user_id = :userId
    """)
    fun update(
        @Bind("id") id: CommentId,
        @Bind("userId") userId: UserId,
        @Bind("newText") newText: String?,
        @Bind("newImage") newImage: String?,
    )

    @Blocking
    @SqlUpdate("DELETE FROM comments WHERE id = :id AND user_id = :userId")
    fun delete(
        @Bind("id") id: CommentId,
        @Bind("userId") userId: UserId,
    )

    @Blocking
    @SqlQuery("""
        SELECT * FROM comments
        WHERE post_id = :postId AND created_at < :olderThan
        ORDER BY created_at
        LIMIT :pageSize
    """)
    fun getAllByPostId(
        @Bind("postId") postId: PostId,
        @Bind("olderThan") olderThan: Instant,
        @Bind("pageSize") pageSize: Int,
    ): List<CommentEntity>

    @Blocking
    @SqlQuery("""
        SELECT * FROM comments
        WHERE user_id = :userId AND created_at < :olderThan
        ORDER BY created_at
        LIMIT :pageSize
    """)
    fun getAllByUserId(
        @Bind("userId") userId: UserId,
        @Bind("olderThan") olderThan: Instant,
        @Bind("pageSize") pageSize: Int,
    ): List<CommentEntity>

    @Blocking
    @SqlQuery("""
        SELECT * FROM comments
        WHERE id = :id
    """)
    fun findById(@Bind("id") id: CommentId): CommentEntity?

}
