package com.tometrics.api.services.socialfeed.db

import com.tometrics.api.common.domain.models.CommentId
import com.tometrics.api.common.domain.models.PostId
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.socialfeed.db.models.CommentEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

interface CommentDao {
    suspend fun insert(
        userId: UserId,
        parentId: CommentId?,
        text: String,
        image: String?,
    ): CommentId?
    suspend fun update(
        id: CommentId,
        userId: UserId,
        newText: String,
        newImage: String?,
    )
    suspend fun delete(
        id: CommentId,
        userId: UserId,
    )
    suspend fun getAllByPostId(
        postId: PostId,
        olderThan: Instant,
        pageSize: Int,
    ): List<CommentEntity>
    suspend fun getAllByUserId(
        userId: UserId,
        olderThan: Instant,
        pageSize: Int,
    ): List<CommentEntity>
    suspend fun findById(id: CommentId): CommentEntity?
    suspend fun increaseReactionCount(id: PostId)
    suspend fun decreaseReactionCount(id: PostId)
}

class DefaultCommentDao(
    private val db: CommentDb,
) : CommentDao {
    override suspend fun insert(
        userId: UserId,
        parentId: CommentId?,
        text: String,
        image: String?,
    ): CommentId? = withContext(Dispatchers.IO) {
        db.insert(
            userId = userId,
            parentId = parentId,
            text = text,
            image = image,
        )
    }
    override suspend fun update(
        id: CommentId,
        userId: UserId,
        newText: String,
        newImage: String?,
    ) = withContext(Dispatchers.IO) {
        db.update(
            id = id,
            userId = userId,
            newText = newText,
            newImage = newImage,
        )
    }
    override suspend fun delete(
        id: CommentId,
        userId: UserId,
    ) = withContext(Dispatchers.IO) {
        db.delete(
            id = id,
            userId = userId,
        )
    }
    override suspend fun getAllByPostId(
        postId: PostId,
        olderThan: Instant,
        pageSize: Int,
    ): List<CommentEntity> = withContext(Dispatchers.IO) {
        db.getAllByPostId(
            postId = postId,
            olderThan = olderThan,
            pageSize = pageSize,
        )
    }
    override suspend fun getAllByUserId(
        userId: UserId,
        olderThan: Instant,
        pageSize: Int,
    ): List<CommentEntity> = withContext(Dispatchers.IO) {
        db.getAllByUserId(
            userId = userId,
            olderThan = olderThan,
            pageSize = pageSize,
        )
    }
    override suspend fun findById(id: CommentId): CommentEntity? = withContext(Dispatchers.IO) {
        db.findById(id = id)
    }
    override suspend fun increaseReactionCount(id: PostId) = withContext(Dispatchers.IO) {
        db.increaseReactionCount(id = id)
    }
    override suspend fun decreaseReactionCount(id: PostId) = withContext(Dispatchers.IO) {
        db.decreaseReactionCount(id = id)
    }

}