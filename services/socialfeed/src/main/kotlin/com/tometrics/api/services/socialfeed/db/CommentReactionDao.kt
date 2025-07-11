package com.tometrics.api.services.socialfeed.db

import com.tometrics.api.common.domain.models.CommentId
import com.tometrics.api.common.domain.models.PostId
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.socialfeed.db.models.CommentReactionEntity
import com.tometrics.api.services.socialfeed.db.models.PostReactionEntity
import com.tometrics.api.services.socialfeed.domain.models.Reaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

interface CommentReactionDao {
    suspend fun insert(
        commentId: CommentId,
        userId: UserId,
        reaction: Reaction,
    ): CommentId?
    suspend fun update(
        commentId: CommentId,
        userId: UserId,
        reaction: Reaction,
    )
    suspend fun delete(
        commentId: CommentId,
        userId: UserId,
    )
    suspend fun getAllByCommentId(
        commentId: CommentId,
        olderThan: Instant,
        pageSize: Int,
    ): List<CommentReactionEntity>
    suspend fun getAllByCommentIdsAndUserId(
        commentIds: Set<CommentId>,
        userId: UserId,
    ): List<CommentReactionEntity>
    suspend fun getLatestDistinctByCommentId(
        commentIds: Set<CommentId>,
    ): List<CommentReactionEntity>
    suspend fun findByCommentIdAndUserId(
        commentId: CommentId,
        userId: UserId,
    ): CommentReactionEntity?
}

class DefaultCommentReactionDao(
    private val db: CommentReactionDb,
) : CommentReactionDao {

    override suspend fun insert(
        commentId: CommentId,
        userId: UserId,
        reaction: Reaction,
    ): CommentId? = withContext(Dispatchers.IO) {
        db.insert(
            commentId = commentId,
            userId = userId,
            reaction = reaction,
        )
    }

    override suspend fun update(
        commentId: CommentId,
        userId: UserId,
        reaction: Reaction,
    ) = withContext(Dispatchers.IO) {
        db.update(
            commentId = commentId,
            userId = userId,
            reaction = reaction,
        )
    }

    override suspend fun delete(
        commentId: CommentId,
        userId: UserId,
    ) = withContext(Dispatchers.IO) {
        db.delete(
            commentId = commentId,
            userId = userId,
        )
    }

    override suspend fun getAllByCommentId(
        commentId: CommentId,
        olderThan: Instant,
        pageSize: Int,
    ): List<CommentReactionEntity> = withContext(Dispatchers.IO) {
        db.getAllByCommentId(
            commentId = commentId,
            olderThan = olderThan,
            pageSize = pageSize,
        )
    }

    override suspend fun getAllByCommentIdsAndUserId(
        commentIds: Set<CommentId>,
        userId: UserId,
    ): List<CommentReactionEntity> = withContext(Dispatchers.IO) {
        db.getAllByCommentIdsAndUserId(
            commentIds = commentIds,
            userId = userId,
        )
    }

    override suspend fun getLatestDistinctByCommentId(
        commentIds: Set<CommentId>,
    ): List<CommentReactionEntity> = withContext(Dispatchers.IO) {
        db.getLatestDistinctByCommentId(commentIds = commentIds)
    }

    override suspend fun findByCommentIdAndUserId(
        commentId: CommentId,
        userId: UserId,
    ): CommentReactionEntity? = withContext(Dispatchers.IO) {
        db.findByCommentIdAndUserId(
            commentId = commentId,
            userId = userId,
        )
    }

}