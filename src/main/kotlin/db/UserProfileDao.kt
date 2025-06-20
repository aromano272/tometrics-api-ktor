package com.tometrics.api.db

import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.db.models.UserProfileEntity
import com.tometrics.api.domain.models.ClimateZone
import com.tometrics.api.domain.models.LocationInfoId

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface UserProfileDao {
    suspend fun findById(userId: UserId): UserProfileEntity?

    suspend fun upsert(
        userId: UserId,
        name: String?,
        locationId: LocationInfoId?,
        metricUnits: Boolean?,
        climateZone: ClimateZone?,
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
        metricUnits: Boolean?,
        climateZone: ClimateZone?,
    ): UserId = withContext(Dispatchers.IO) {
        db.upsert(
            userId,
            name,
            locationId,
            metricUnits,
            climateZone,
        )
    }

}
