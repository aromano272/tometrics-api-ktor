package com.tometrics.api.service

import com.tometrics.api.db.GeoNameCity500Dao
import com.tometrics.api.db.UserProfileDao
import com.tometrics.api.db.models.toDomain
import com.tometrics.api.db.models.toLocationInfo
import com.tometrics.api.domain.models.LocationInfoId
import com.tometrics.api.domain.models.Requester
import com.tometrics.api.domain.models.domain.models.UserProfile

interface UserProfileService {
    suspend fun get(requester: Requester): UserProfile
    suspend fun update(
        requester: Requester,
        name: String?,
        locationId: LocationInfoId?,
    ): UserProfile
}

class DefaultUserProfileService(
    private val userProfileDao: UserProfileDao,
    private val city500Dao: GeoNameCity500Dao,
) : UserProfileService {

    override suspend fun get(requester: Requester): UserProfile {
        val entity = (userProfileDao.findById(requester.userId)
            ?: run {
                userProfileDao.upsert(requester.userId, null, null)
                userProfileDao.findById(requester.userId)!!
            })

        val location = entity.locationId?.let { city500Dao.getById(it) }?.toLocationInfo()

        return entity.toDomain(location)
    }

    override suspend fun update(
        requester: Requester,
        name: String?,
        locationId: LocationInfoId?,
    ): UserProfile {
        userProfileDao.upsert(requester.userId, name, locationId)
        return get(requester)
    }


}