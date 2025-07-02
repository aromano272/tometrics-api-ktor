package com.tometrics.api.services.user.services

import com.tometrics.api.services.commongrpc.models.user.toNetwork
import com.tometrics.api.services.commongrpc.services.UserGrpcService
import com.tometrics.api.services.protos.UserGrpcServiceGrpcKt
import com.tometrics.api.services.protos.ValidateUserIdsRequest
import com.tometrics.api.services.protos.ValidateUserIdsResponse


class DefaultUserGrpcService(
    private val service: UserGrpcService,
) : UserGrpcServiceGrpcKt.UserGrpcServiceCoroutineImplBase() {

    override suspend fun validateUserIds(request: ValidateUserIdsRequest): ValidateUserIdsResponse =
        service.validateUserIds(request.userIdsList.toSet()).toNetwork()

}