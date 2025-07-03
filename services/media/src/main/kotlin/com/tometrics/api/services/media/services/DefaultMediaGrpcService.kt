package com.tometrics.api.services.media.services

import com.google.protobuf.BoolValue
import com.tometrics.api.services.commongrpc.services.MediaGrpcService
import com.tometrics.api.services.protos.MediaGrpcServiceGrpcKt
import com.tometrics.api.services.protos.ValidateMediaUrlRequest


class DefaultMediaGrpcService(
    private val service: MediaGrpcService,
) : MediaGrpcServiceGrpcKt.MediaGrpcServiceCoroutineImplBase() {

    override suspend fun validateMediaUrl(request: ValidateMediaUrlRequest): BoolValue =
        service.validateMediaUrl(request.requesterId, request.url)
            .let { BoolValue.of(it) }

}