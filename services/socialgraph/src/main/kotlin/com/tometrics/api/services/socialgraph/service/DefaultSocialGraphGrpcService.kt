package com.tometrics.api.services.socialgraph.service

import com.google.protobuf.Int32Value
import com.tometrics.api.services.commongrpc.services.SocialGraphGrpcService
import com.tometrics.api.services.protos.SocialConnections
import com.tometrics.api.services.protos.SocialGraphGrpcServiceGrpcKt


class DefaultSocialGraphGrpcService(
    private val service: SocialGraphGrpcService,
) : SocialGraphGrpcServiceGrpcKt.SocialGraphGrpcServiceCoroutineImplBase() {

    override suspend fun getConnectionsByUserId(request: Int32Value): SocialConnections =
        service.grpcGetConnectionsByUserId(request.value).toNetwork()

}