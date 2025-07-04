package com.tometrics.api.services.commongrpc.models.socialgraph

import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.protos.SocialConnections

data class GrpcSocialConnections(
    val followers: List<UserId>,
    val following: List<UserId>,
) {
    fun toNetwork(): SocialConnections = SocialConnections.newBuilder()
        .addAllFollowers(followers)
        .addAllFollowing(following)
        .build()

    companion object {
        fun fromNetwork(network: SocialConnections): GrpcSocialConnections = GrpcSocialConnections(
            followers = network.followersList,
            following = network.followingList,
        )
    }
}