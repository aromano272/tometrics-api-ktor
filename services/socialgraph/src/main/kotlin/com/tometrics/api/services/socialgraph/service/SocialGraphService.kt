package com.tometrics.api.services.socialgraph.service

import com.tometrics.api.common.domain.models.UnauthorizedError
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.commongrpc.models.user.GrpcValidateUsersResult
import com.tometrics.api.services.commongrpc.services.UserGrpcClient
import com.tometrics.api.services.socialgraph.db.FollowerDao
import com.tometrics.api.services.socialgraph.domain.models.SocialConnections
import io.ktor.util.logging.*

interface SocialGraphService {

    suspend fun getConnectionsByUserId(userId: UserId): SocialConnections
    suspend fun follow(requesterId: UserId, userId: UserId)
    suspend fun unfollow(requesterId: UserId, userId: UserId)

}

class DefaultSocialGraphService(
    private val logger: Logger,
    private val userGrpcClient: UserGrpcClient,
    private val dao: FollowerDao,
) : SocialGraphService {

    override suspend fun getConnectionsByUserId(userId: UserId): SocialConnections {
        val following = dao.getAllFollowedByUserId(userId)
        val followers = dao.getAllFollowersOfUserId(userId)

        return SocialConnections(
            followers = followers.map { it.userId },
            following = following.map { it.followedUserId },
        )
    }

    override suspend fun follow(requesterId: UserId, userId: UserId) {
        val result = userGrpcClient.validateUserIds(setOf(requesterId, userId))
            .takeIf { it !is GrpcValidateUsersResult.Success }
            ?.let { throw UnauthorizedError("") }
        dao.insert(requesterId, userId)
    }

    override suspend fun unfollow(requesterId: UserId, userId: UserId) {
        val result = userGrpcClient.validateUserIds(setOf(requesterId, userId))
            .takeIf { it !is GrpcValidateUsersResult.Success }
            ?.let { throw UnauthorizedError("") }
        dao.delete(requesterId, userId)
    }

}

