package com.tometrics.api.db

import com.tometrics.api.db.UserProfileDb
import com.tometrics.api.db.models.UserProfileEntity
import com.tometrics.api.domain.models.LocationInfoId
import com.tometrics.api.domain.models.UserId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface UserProfileDao {
    suspend fun findById(userId: UserId): UserProfileEntity?

    suspend fun upsert(
        userId: UserId,
        name: String?,
        locationId: LocationInfoId?,
    ): Int

}

class DefaultUserProfileDao(
    private val db: UserProfileDb
) : UserProfileDao {

    override suspend fun findById(userId: UserId): UserProfileEntity? = withContext(Dispatchers.IO) {
        db.findById(userId)
    }

    override suspend fun upsert(
        userId: UserId,
        name: String?,
        locationId: LocationInfoId?,
    ): UserId = withContext(Dispatchers.IO) {
        db.upsert(
            userId,
            name,
            locationId
        )
    }

}
