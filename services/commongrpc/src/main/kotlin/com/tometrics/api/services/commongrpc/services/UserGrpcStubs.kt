package com.tometrics.api.services.commongrpc.services

import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.commongrpc.models.user.GrpcValidateUsersResult
import com.tometrics.api.services.protos.UserGrpcServiceGrpcKt
import com.tometrics.api.services.protos.validateUserIdsRequest

interface UserGrpcService {
    suspend fun validateUserIds(userIds: Set<UserId>): GrpcValidateUsersResult
}

interface UserGrpcClient : UserGrpcService

class DefaultUserGrpcClient(
    private val client: GrpcLazyClient<UserGrpcServiceGrpcKt.UserGrpcServiceCoroutineStub>,
) : UserGrpcClient {

    override suspend fun validateUserIds(userIds: Set<UserId>): GrpcValidateUsersResult {
        val response = client.await().validateUserIds(validateUserIdsRequest {
            this.userIds += userIds
        })
        return GrpcValidateUsersResult.fromNetwork(response)
    }

}