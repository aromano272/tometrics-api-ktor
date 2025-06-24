package com.tometrics.api.services.socialgraph.service

import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.socialgraph.db.FollowerDao
import com.tometrics.api.services.socialgraph.domain.models.SocialConnections
import com.tometrics.api.userrpc.UserRpcService

interface SocialGraphService {

    suspend fun getConnectionsByUserId(userId: UserId): SocialConnections
    suspend fun follow(requesterId: UserId, userId: UserId)
    suspend fun unfollow(requesterId: UserId, userId: UserId)

}

class DefaultSocialGraphService(
    private val userRpcService: UserRpcService,
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
        userRpcService.validateUserIds(setOf(requesterId, userId))
        dao.insert(requesterId, userId)
    }

    override suspend fun unfollow(requesterId: UserId, userId: UserId) {
        userRpcService.validateUserIds(setOf(requesterId, userId))
        dao.delete(requesterId, userId)
    }

}

