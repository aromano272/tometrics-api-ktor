package com.tometrics.api.services.media.services

import com.google.protobuf.BoolValue
import com.tometrics.api.services.commongrpc.models.user.toNetwork
import com.tometrics.api.services.protos.MediaGrpcServiceGrpcKt
import com.tometrics.api.services.protos.ValidateMediaUrlRequest
import com.tometrics.api.services.protos.ValidateMediaUrlsRequest
import com.tometrics.api.services.protos.ValidateMediaUrlsResponse


class DefaultMediaGrpcService(
    private val service: MediaService,
) : MediaGrpcServiceGrpcKt.MediaGrpcServiceCoroutineImplBase() {

    override suspend fun validateMediaUrl(request: ValidateMediaUrlRequest): BoolValue =
        service.validateMediaUrl(request.requesterId, request.url)
            .let { BoolValue.of(it) }

    override suspend fun validateMediaUrls(request: ValidateMediaUrlsRequest): ValidateMediaUrlsResponse =
        service.validateMediaUrls(request.requesterId, request.urlList.toSet())
            .toNetwork()

}