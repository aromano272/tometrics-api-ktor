package com.tometrics.api.services.socialfeed.db

import com.tometrics.api.common.domain.models.ImageUrl
import com.tometrics.api.common.domain.models.PostId
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.socialfeed.db.models.PostEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

interface PostDao {
    suspend fun insert(
        userId: UserId,
        images: List<ImageUrl>,
        text: String?,
    ): PostId?
    suspend fun update(
        id: PostId,
        userId: UserId,
        newImages: List<ImageUrl>?,
        newText: String?,
    )
    suspend fun delete(
        id: PostId,
        userId: UserId,
    )
    suspend fun getFeed(
        olderThan: Instant,
        pageSize: Int = 20,
    ): List<PostEntity>
    suspend fun getAllByUserId(
        userId: UserId,
        olderThan: Instant,
        pageSize: Int = 20,
    ): List<PostEntity>
    suspend fun findById(id: PostId): List<PostEntity>
}

class DefaultPostDao(
    private val db: PostDb,
) : PostDao {

    override suspend fun insert(
        userId: UserId,
        images: List<ImageUrl>,
        text: String?,
    ): PostId? = withContext(Dispatchers.IO) {
        db.insert(
            userId = userId,
            images = images,
            text = text,
        )
    }
    override suspend fun update(
        id: PostId,
        userId: UserId,
        newImages: List<ImageUrl>?,
        newText: String?,
    ) = withContext(Dispatchers.IO) {
        db.update(
            id = id,
            userId = userId,
            newImages = newImages,
            newText = newText,
        )
    }
    override suspend fun delete(
        id: PostId,
        userId: UserId,
    ) = withContext(Dispatchers.IO) {
        db.delete(
            id = id,
            userId = userId,
        )
    }
    override suspend fun getFeed(
        olderThan: Instant,
        pageSize: Int,
    ): List<PostEntity> = withContext(Dispatchers.IO) {
        db.getFeed(
            olderThan = olderThan,
            pageSize = pageSize,
        )
    }
    override suspend fun getAllByUserId(
        userId: UserId,
        olderThan: Instant,
        pageSize: Int,
    ): List<PostEntity> = withContext(Dispatchers.IO) {
        db.getAllByUserId(
            userId = userId,
            olderThan = olderThan,
            pageSize = pageSize,
        )
    }
    override suspend fun findById(id: PostId): List<PostEntity> = withContext(Dispatchers.IO) {
        db.findById(id = id)
    }

}