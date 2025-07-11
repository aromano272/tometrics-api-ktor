package com.tometrics.api.services.commongrpc.services

import com.tometrics.api.common.domain.models.ImageUrl
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.commongrpc.models.user.GrpcValidateMediaUrlsResult
import com.tometrics.api.services.protos.MediaGrpcServiceGrpcKt
import com.tometrics.api.services.protos.ValidateMediaUrlRequest
import com.tometrics.api.services.protos.ValidateMediaUrlsRequest

interface MediaGrpcClient {
    suspend fun validateMediaUrl(requesterId: UserId, url: ImageUrl): Boolean
    suspend fun validateMediaUrls(requesterId: UserId, urls: Set<ImageUrl>): GrpcValidateMediaUrlsResult
}

class DefaultMediaGrpcClient(
    private val client: GrpcLazyClient<MediaGrpcServiceGrpcKt.MediaGrpcServiceCoroutineStub>,
) : MediaGrpcClient {

    override suspend fun validateMediaUrl(requesterId: UserId, url: ImageUrl): Boolean {
        val request = ValidateMediaUrlRequest.newBuilder()
            .setRequesterId(requesterId)
            .setUrl(url)
            .build()
        val response = client.await().validateMediaUrl(request)
        return response.value
    }

    override suspend fun validateMediaUrls(requesterId: UserId, urls: Set<ImageUrl>): GrpcValidateMediaUrlsResult {
        val request = ValidateMediaUrlsRequest.newBuilder()
            .setRequesterId(requesterId)
            .addAllUrl(urls)
            .build()
        val response = client.await().validateMediaUrls(request)
        return GrpcValidateMediaUrlsResult.fromNetwork(response)
    }

}