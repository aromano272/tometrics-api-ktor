package com.tometrics.api.services.socialgraph.service

import com.google.protobuf.Int32Value
import com.tometrics.api.services.commongrpc.models.socialgraph.GrpcSocialConnections
import com.tometrics.api.services.protos.SocialConnections
import com.tometrics.api.services.protos.SocialGraphGrpcServiceGrpcKt


class DefaultSocialGraphGrpcService(
    private val service: SocialGraphService,
) : SocialGraphGrpcServiceGrpcKt.SocialGraphGrpcServiceCoroutineImplBase() {

    override suspend fun getConnectionsByUserId(request: Int32Value): SocialConnections =
        service.getConnectionsByUserId(request.value).toGrpc().toNetwork()

}

private fun com.tometrics.api.services.socialgraph.domain.models.SocialConnections.toGrpc(): GrpcSocialConnections =
    GrpcSocialConnections(
        followers = followers,
        following = following,
    )

