package com.tometrics.api.services.socialfeed.db

import com.tometrics.api.common.domain.models.PostId
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.socialfeed.db.models.PostReactionEntity
import com.tometrics.api.services.socialfeed.domain.models.Reaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

interface PostReactionDao {
    suspend fun insert(
        postId: PostId,
        userId: UserId,
        reaction: Reaction,
    ): PostId?
    suspend fun update(
        postId: PostId,
        userId: UserId,
        reaction: Reaction,
    )
    suspend fun delete(
        postId: PostId,
        userId: UserId,
    )
    suspend fun getAllByPostId(
        postId: PostId,
        olderThan: Instant,
        pageSize: Int,
    ): List<PostReactionEntity>
    suspend fun getAllByPostIdsAndUserId(
        postIds: Set<PostId>,
        userId: UserId,
    ): List<PostReactionEntity>
    suspend fun getLatestDistinctByPostId(
        postIds: Set<PostId>,
    ): List<PostReactionEntity>
    suspend fun findByPostIdAndUserId(
        postId: PostId,
        userId: UserId,
    ): PostReactionEntity?
}

class DefaultPostReactionDao(
    private val db: PostReactionDb,
) : PostReactionDao {

    override suspend fun insert(
        postId: PostId,
        userId: UserId,
        reaction: Reaction,
    ): PostId? = withContext(Dispatchers.IO) {
        db.insert(
            postId = postId,
            userId = userId,
            reaction = reaction,
        )
    }

    override suspend fun update(
        postId: PostId,
        userId: UserId,
        reaction: Reaction,
    ) = withContext(Dispatchers.IO) {
        db.update(
            postId = postId,
            userId = userId,
            reaction = reaction,
        )
    }

    override suspend fun delete(
        postId: PostId,
        userId: UserId,
    ) = withContext(Dispatchers.IO) {
        db.delete(
            postId = postId,
            userId = userId,
        )
    }

    override suspend fun getAllByPostId(
        postId: PostId,
        olderThan: Instant,
        pageSize: Int,
    ): List<PostReactionEntity> = withContext(Dispatchers.IO) {
        db.getAllByPostId(
            postId = postId,
            olderThan = olderThan,
            pageSize = pageSize,
        )
    }

    override suspend fun getAllByPostIdsAndUserId(
        postIds: Set<PostId>,
        userId: UserId,
    ): List<PostReactionEntity> = withContext(Dispatchers.IO) {
        db.getAllByPostIdsAndUserId(
            postIds = postIds,
            userId = userId,
        )
    }

    override suspend fun getLatestDistinctByPostId(
        postIds: Set<PostId>,
    ): List<PostReactionEntity> = withContext(Dispatchers.IO) {
        db.getLatestDistinctByPostId(postIds = postIds)
    }

    override suspend fun findByPostIdAndUserId(
        postId: PostId,
        userId: UserId,
    ): PostReactionEntity? = withContext(Dispatchers.IO) {
        db.findByPostIdAndUserId(
            postId = postId,
            userId = userId,
        )
    }

}