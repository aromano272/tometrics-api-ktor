package com.tometrics.api.services.user.services

import com.tometrics.api.services.commongrpc.models.user.toNetwork
import com.tometrics.api.services.commongrpc.services.UserGrpcService
import com.tometrics.api.services.protos.*


class DefaultUserGrpcService(
    private val service: UserGrpcService,
) : UserGrpcServiceGrpcKt.UserGrpcServiceCoroutineImplBase() {

    override suspend fun validateUserIds(request: ValidateUserIdsRequest): ValidateUserIdsResponse =
        service.validateUserIds(request.userIdsList.toSet()).toNetwork()

    override suspend fun getAllByIds(request: GetAllByIdsRequest): GetAllByIdsResponse {
        val result = service.getAllByIds(request.userIdsList.toSet())
        val protos = result.map { it.toNetwork() }
        return GetAllByIdsResponse.newBuilder()
            .addAllUsers(protos)
            .build()
    }
}