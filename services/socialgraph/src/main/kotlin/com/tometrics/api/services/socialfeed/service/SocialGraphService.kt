package com.tometrics.api.services.socialfeed.service

import com.tometrics.api.common.domain.models.UnauthorizedError
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.commongrpc.models.socialgraph.GrpcSocialConnections
import com.tometrics.api.services.commongrpc.models.user.GrpcValidateUsersResult
import com.tometrics.api.services.commongrpc.services.SocialGraphGrpcService
import com.tometrics.api.services.commongrpc.services.UserGrpcClient
import com.tometrics.api.services.socialfeed.db.FollowerDao
import com.tometrics.api.services.socialfeed.domain.models.SocialConnections
import io.ktor.util.logging.*

interface SocialGraphService : SocialGraphGrpcService {

    suspend fun getConnectionsByUserId(userId: UserId): SocialConnections
    suspend fun follow(requesterId: UserId, userId: UserId)
    suspend fun unfollow(requesterId: UserId, userId: UserId)

}

class DefaultSocialGraphService(
    private val logger: Logger,
    private val userGrpcClient: UserGrpcClient,
    private val dao: FollowerDao,
) : SocialGraphService {

    override suspend fun grpcGetConnectionsByUserId(userId: UserId): GrpcSocialConnections =
        getConnectionsByUserId(userId).toGrpc()

    override suspend fun getConnectionsByUserId(userId: UserId): SocialConnections {
        val following = dao.getAllFollowedByUserId(userId)
        val followers = dao.getAllFollowersOfUserId(userId)

        return SocialConnections(
            followers = followers.map { it.userId },
            following = following.map { it.followedUserId },
        )
    }

    override suspend fun follow(requesterId: UserId, userId: UserId) {
        userGrpcClient.validateUserIds(setOf(requesterId, userId))
            .takeIf { it !is GrpcValidateUsersResult.Success }
            ?.let { throw UnauthorizedError("") }
        dao.insert(requesterId, userId)
    }

    override suspend fun unfollow(requesterId: UserId, userId: UserId) {
        userGrpcClient.validateUserIds(setOf(requesterId, userId))
            .takeIf { it !is GrpcValidateUsersResult.Success }
            ?.let { throw UnauthorizedError("") }
        dao.delete(requesterId, userId)
    }

}

private fun SocialConnections.toGrpc(): GrpcSocialConnections =
    GrpcSocialConnections(
        followers = followers,
        following = following,
    )

