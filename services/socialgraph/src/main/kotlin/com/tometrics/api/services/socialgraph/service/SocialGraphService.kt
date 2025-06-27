package com.tometrics.api.services.socialgraph.service

import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.protos.UserGrpcServiceGrpcKt
import com.tometrics.api.services.protos.validateUserIdsRequest
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
    private val userGrpcClient: UserGrpcServiceGrpcKt.UserGrpcServiceCoroutineStub,
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
        logger.info("follow(requesterId: $requesterId, userId: $userId)")
        val result = userGrpcClient.validateUserIds(validateUserIdsRequest {
            userIds += setOf(requesterId, userId)
        })
        logger.info("follow(requesterId: $requesterId, userId: $userId) result: $result")
        dao.insert(requesterId, userId)
    }

    override suspend fun unfollow(requesterId: UserId, userId: UserId) {
        logger.info("unfollow(requesterId: $requesterId, userId: $userId)")
        val result = userGrpcClient.validateUserIds(validateUserIdsRequest {
            userIds += setOf(requesterId, userId)
        })
        logger.info("unfollow(requesterId: $requesterId, userId: $userId) result: $result")
        dao.delete(requesterId, userId)
    }

}

