package com.tometrics.api.services.commongrpc.models.user

import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.protos.ValidateUserIdsResponse
import com.tometrics.api.services.protos.validateUserIdsResponse
import com.tometrics.api.services.protos.validateUsersNotFound
import com.tometrics.api.services.protos.validateUsersSuccess


sealed interface GrpcValidateUsersResult {
    data object Success : GrpcValidateUsersResult
    data class UserIdsNotFound(val missingUserIds: Set<UserId>) : GrpcValidateUsersResult

    companion object {
        fun fromNetwork(network: ValidateUserIdsResponse): GrpcValidateUsersResult = when (network.resultCase) {
            ValidateUserIdsResponse.ResultCase.SUCCESS -> Success
            ValidateUserIdsResponse.ResultCase.NOT_FOUND ->
                UserIdsNotFound(network.notFound.missingUserIdsList.toSet())
            ValidateUserIdsResponse.ResultCase.RESULT_NOT_SET -> throw IllegalStateException()
        }
    }
}

fun GrpcValidateUsersResult.toNetwork(): ValidateUserIdsResponse = validateUserIdsResponse {
    when (this@toNetwork) {
        GrpcValidateUsersResult.Success -> success = validateUsersSuccess {}
        is GrpcValidateUsersResult.UserIdsNotFound -> notFound = validateUsersNotFound {
            missingUserIds += this@toNetwork.missingUserIds
        }
    }
}
