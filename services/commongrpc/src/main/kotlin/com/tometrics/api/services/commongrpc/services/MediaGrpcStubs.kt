package com.tometrics.api.services.commongrpc.services

import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.protos.MediaGrpcServiceGrpcKt
import com.tometrics.api.services.protos.ValidateMediaUrlRequest

interface MediaGrpcService {
    suspend fun validateMediaUrl(requesterId: UserId, url: String): Boolean
}

interface MediaGrpcClient : MediaGrpcService

class DefaultMediaGrpcClient(
    private val client: GrpcLazyClient<MediaGrpcServiceGrpcKt.MediaGrpcServiceCoroutineStub>,
) : MediaGrpcClient {

    override suspend fun validateMediaUrl(requesterId: UserId, url: String): Boolean {
        val request = ValidateMediaUrlRequest.newBuilder()
            .setRequesterId(requesterId)
            .setUrl(url)
            .build()
        val response = client.await().validateMediaUrl(request)
        return response.value
    }

}