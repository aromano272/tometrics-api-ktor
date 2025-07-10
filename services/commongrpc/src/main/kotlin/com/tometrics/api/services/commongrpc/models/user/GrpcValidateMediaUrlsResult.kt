package com.tometrics.api.services.commongrpc.models.user

import com.tometrics.api.common.domain.models.ImageUrl
import com.tometrics.api.services.protos.ValidateMediaUrlsResponse
import com.tometrics.api.services.protos.validateMediaUrlsNotFound
import com.tometrics.api.services.protos.validateMediaUrlsResponse
import com.tometrics.api.services.protos.validateMediaUrlsSuccess


sealed interface GrpcValidateMediaUrlsResult {
    data object Success : GrpcValidateMediaUrlsResult
    data class MediaUrlsNotFound(val missingMediaUrls: Set<ImageUrl>) : GrpcValidateMediaUrlsResult

    companion object {
        fun fromNetwork(network: ValidateMediaUrlsResponse): GrpcValidateMediaUrlsResult = when (network.resultCase) {
            ValidateMediaUrlsResponse.ResultCase.SUCCESS -> Success
            ValidateMediaUrlsResponse.ResultCase.NOT_FOUND ->
                MediaUrlsNotFound(network.notFound.missingMediaUrlsList.toSet())
            ValidateMediaUrlsResponse.ResultCase.RESULT_NOT_SET -> throw IllegalStateException()
        }
    }
}

fun GrpcValidateMediaUrlsResult.toNetwork(): ValidateMediaUrlsResponse = validateMediaUrlsResponse {
    when (this@toNetwork) {
        GrpcValidateMediaUrlsResult.Success -> success = validateMediaUrlsSuccess {}
        is GrpcValidateMediaUrlsResult.MediaUrlsNotFound -> notFound = validateMediaUrlsNotFound {
            missingMediaUrls += this@toNetwork.missingMediaUrls
        }
    }
}
