package com.tometrics.api.services.user.services

import com.tometrics.api.auth.domain.models.Requester
import com.tometrics.api.common.domain.models.LocationInfoId
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.commongrpc.models.user.GrpcValidateUsersResult
import com.tometrics.api.services.commongrpc.services.UserGrpcService
import com.tometrics.api.services.user.db.GeoNameCity500Dao
import com.tometrics.api.services.user.db.UserDao
import com.tometrics.api.services.user.db.models.toDomain
import com.tometrics.api.services.user.db.models.toLocationInfo
import com.tometrics.api.services.user.domain.models.ClimateZone
import com.tometrics.api.services.user.domain.models.User
import io.ktor.util.logging.*

interface UserService : UserGrpcService {

    suspend fun get(requester: Requester): User
    suspend fun get(userId: UserId): User
    suspend fun update(
        requester: Requester,
        name: String?,
        locationId: LocationInfoId?,
        metricUnits: Boolean?,
        climateZone: ClimateZone?,
    ): User

}

class DefaultUserService(
    private val logger: Logger,
    private val userDao: UserDao,
    private val city500Dao: GeoNameCity500Dao,
) : UserService {

    override suspend fun validateUserIds(userIds: Set<UserId>): GrpcValidateUsersResult {
        logger.info("validateUserIds(userIds: {})", userIds)
        val all = userDao.getAllByIds(userIds)
        val foundIds = all.map { it.id }.toSet()
        val missingIds = userIds - foundIds
        if (missingIds.isNotEmpty()) return GrpcValidateUsersResult.UserIdsNotFound(missingIds)
        return GrpcValidateUsersResult.Success
    }


    override suspend fun get(requester: Requester): User =
        get(requester.userId)

    override suspend fun get(userId: UserId): User {
        val entity = (userDao.findById(userId)
            ?: throw IllegalStateException("User not found: $userId"))

        val location = entity.locationId?.let { city500Dao.getById(it) }?.toLocationInfo()

        return entity.toDomain(location)
    }

    override suspend fun update(
        requester: Requester,
        name: String?,
        locationId: LocationInfoId?,
        metricUnits: Boolean?,
        climateZone: ClimateZone?,
    ): User {
        userDao.update(requester.userId, name, null, null, null, null, locationId, metricUnits, climateZone)
        return get(requester)
    }

}

// TODO(aromano): probably migrate all service(data layer)-level methods to Outcome
sealed interface ValidateUsersResult {
    data object Success : ValidateUsersResult
    data class UserIdsNotFound(val missingUserIds: Set<UserId>) : ValidateUsersResult
}