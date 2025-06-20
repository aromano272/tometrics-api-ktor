package com.tometrics.api.service

import com.tometrics.api.auth.domain.models.Requester
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.db.GeoNameCity500Dao
import com.tometrics.api.db.UserProfileDao
import com.tometrics.api.db.models.toDomain
import com.tometrics.api.db.models.toLocationInfo
import com.tometrics.api.domain.models.*

interface UserProfileService {
    suspend fun get(requester: Requester): UserProfile
    suspend fun get(userId: UserId): UserProfile
    suspend fun update(
        requester: Requester,
        name: String?,
        locationId: LocationInfoId?,
        metricUnits: Boolean?,
        climateZone: ClimateZone?,
    ): UserProfile
}

class DefaultUserProfileService(
    private val userProfileDao: UserProfileDao,
    private val city500Dao: GeoNameCity500Dao,
) : UserProfileService {

    override suspend fun get(requester: Requester): UserProfile =
        get(requester.userId)

    override suspend fun get(userId: UserId): UserProfile {
        val entity = (userProfileDao.findById(userId)
            ?: run {
                userProfileDao.upsert(userId, null, null, true, null)
                userProfileDao.findById(userId)!!
            })

        val location = entity.locationId?.let { city500Dao.getById(it) }?.toLocationInfo()

        return entity.toDomain(location)
    }

    override suspend fun update(
        requester: Requester,
        name: String?,
        locationId: LocationInfoId?,
        metricUnits: Boolean?,
        climateZone: ClimateZone?,
    ): UserProfile {
        userProfileDao.upsert(requester.userId, name, locationId, metricUnits, climateZone)
        return get(requester)
    }


}