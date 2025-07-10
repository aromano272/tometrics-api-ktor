package com.tometrics.api.services.user.services

import com.tometrics.api.auth.domain.models.Requester
import com.tometrics.api.common.domain.models.ClimateZone
import com.tometrics.api.common.domain.models.LocationInfoId
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.commongrpc.services.SocialGraphGrpcClient
import com.tometrics.api.services.user.db.GeoNameCity500Dao
import com.tometrics.api.services.user.db.UserDao
import com.tometrics.api.services.user.db.models.toDomain
import com.tometrics.api.services.user.db.models.toLocationInfo
import com.tometrics.api.services.user.domain.models.User
import com.tometrics.api.services.user.domain.models.UserWithSocialConnections
import io.ktor.util.logging.*

interface UserService {

    suspend fun findById(id: UserId): User?
    suspend fun validateUserIds(userIds: Set<UserId>): ValidateUsersResult
    suspend fun getAllByIds(userIds: Set<UserId>): List<User>

    suspend fun get(requester: Requester): UserWithSocialConnections
    suspend fun get(userId: UserId): UserWithSocialConnections
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
    private val socialGraphGrpcClient: SocialGraphGrpcClient,
    private val userDao: UserDao,
    private val city500Dao: GeoNameCity500Dao,
) : UserService {

    override suspend fun findById(id: UserId): User? {
        val entity = userDao.findById(id)
        val location = entity?.locationId?.let { city500Dao.getById(it) }
        return entity?.toDomain(location?.toLocationInfo())
    }

    override suspend fun validateUserIds(userIds: Set<UserId>): ValidateUsersResult {
        logger.info("validateUserIds(userIds: {})", userIds)
        val all = userDao.getAllByIds(userIds)
        val foundIds = all.map { it.id }.toSet()
        val missingIds = userIds - foundIds
        if (missingIds.isNotEmpty()) return ValidateUsersResult.UserIdsNotFound(missingIds)
        return ValidateUsersResult.Success
    }

    override suspend fun getAllByIds(userIds: Set<UserId>): List<User> {
        val userEntities = userDao.getAllByIds(userIds)
        val locationsMap = city500Dao.getAllByIds(userEntities.mapNotNull { it.locationId }.toSet())
            .associateBy { it.geonameid }

        val users = userEntities.map { user ->
            val location = locationsMap[user.locationId]
            user.toDomain(location?.toLocationInfo(null))
        }
        return users
    }


    override suspend fun get(requester: Requester): UserWithSocialConnections =
        get(requester.userId)

    override suspend fun get(userId: UserId): UserWithSocialConnections {
        val entity = (userDao.findById(userId)
            ?: throw IllegalStateException("User not found: $userId"))

        val location = entity.locationId?.let { city500Dao.getById(it) }?.toLocationInfo()

        val connections = socialGraphGrpcClient.grpcGetConnectionsByUserId(userId)
        val followers = userDao.getAllByIds(connections.followers.toSet())
            .map { it.toDomain(null) }
        val following = userDao.getAllByIds(connections.following.toSet())
            .map { it.toDomain(null) }

        val user = entity.toDomain(location)

        return UserWithSocialConnections(
            user = user,
            followers = followers,
            following = following,
        )
    }

    override suspend fun update(
        requester: Requester,
        name: String?,
        locationId: LocationInfoId?,
        metricUnits: Boolean?,
        climateZone: ClimateZone?,
    ): User {
        userDao.update(requester.userId, name, null, null, null, null, locationId, metricUnits, climateZone)
        val entity = (userDao.findById(requester.userId)
            ?: throw IllegalStateException("User not found: ${requester.userId}"))

        val location = entity.locationId?.let { city500Dao.getById(it) }?.toLocationInfo()

        return entity.toDomain(location)
    }

}

// TODO(aromano): probably migrate all service(data layer)-level methods to Outcome
sealed interface ValidateUsersResult {
    data object Success : ValidateUsersResult
    data class UserIdsNotFound(val missingUserIds: Set<UserId>) : ValidateUsersResult
}
