package com.tometrics.api.service

import com.tometrics.api.db.UserProfileDao
import com.tometrics.api.db.models.toDomain
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
) : UserProfileService {

    // TODO: create java migration to insert all existing users into userprofiles

    override suspend fun get(requester: Requester): UserProfile =
        (userProfileDao.findById(requester.userId)
            ?: run {
                userProfileDao.upsert(requester.userId, null, null)
                userProfileDao.findById(requester.userId)!!
            })?.toDomain()

}