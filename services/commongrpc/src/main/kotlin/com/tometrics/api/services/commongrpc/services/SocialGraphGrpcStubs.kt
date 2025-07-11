package com.tometrics.api.services.commongrpc.services

import com.google.protobuf.Int32Value
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.commongrpc.models.socialgraph.GrpcSocialConnections
import com.tometrics.api.services.protos.SocialGraphGrpcServiceGrpcKt

interface SocialGraphGrpcClient {
    suspend fun grpcGetConnectionsByUserId(userId: UserId): GrpcSocialConnections
}

class DefaultSocialGraphGrpcClient(
    private val client: GrpcLazyClient<SocialGraphGrpcServiceGrpcKt.SocialGraphGrpcServiceCoroutineStub>,
) : SocialGraphGrpcClient {

    override suspend fun grpcGetConnectionsByUserId(userId: UserId): GrpcSocialConnections =
        client.await().getConnectionsByUserId(Int32Value.of(userId)).let {
            GrpcSocialConnections.fromNetwork(it)
        }

}