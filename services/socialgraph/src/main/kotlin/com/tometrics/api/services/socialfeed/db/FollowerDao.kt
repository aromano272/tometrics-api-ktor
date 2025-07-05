package com.tometrics.api.services.socialfeed.db

import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.socialfeed.db.models.FollowerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface FollowerDao {
    suspend fun insert(userId: UserId, followedUserId: UserId): Int?
    suspend fun delete(userId: UserId, followedUserId: UserId)
    suspend fun getAllFollowedByUserId(userId: UserId): List<FollowerEntity>
    suspend fun getAllFollowersOfUserId(userId: UserId): List<FollowerEntity>
}

class DefaultFollowerDao(
    private val db: FollowerDb
) : FollowerDao {

    override suspend fun insert(userId: UserId, followedUserId: UserId): Int? = withContext(Dispatchers.IO) {
        db.insert(userId, followedUserId)
    }

    override suspend fun delete(userId: UserId, followedUserId: UserId) = withContext(Dispatchers.IO) {
        db.delete(userId, followedUserId)
    }

    override suspend fun getAllFollowedByUserId(userId: UserId): List<FollowerEntity> = withContext(Dispatchers.IO) {
        db.getAllFollowedByUserId(userId)
    }

    override suspend fun getAllFollowersOfUserId(userId: UserId): List<FollowerEntity> = withContext(Dispatchers.IO) {
        db.getAllFollowersOfUserId(userId)
    }
}