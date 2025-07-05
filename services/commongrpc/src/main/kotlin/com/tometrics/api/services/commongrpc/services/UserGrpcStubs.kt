package com.tometrics.api.services.commongrpc.services

import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.commongrpc.models.user.GrpcUser
import com.tometrics.api.services.commongrpc.models.user.GrpcValidateUsersResult
import com.tometrics.api.services.protos.GetAllByIdsRequest
import com.tometrics.api.services.protos.UserGrpcServiceGrpcKt
import com.tometrics.api.services.protos.validateUserIdsRequest

interface UserGrpcService {
    suspend fun validateUserIds(userIds: Set<UserId>): GrpcValidateUsersResult
    suspend fun getAllByIds(userIds: Set<UserId>): List<GrpcUser>
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

    override suspend fun getAllByIds(userIds: Set<UserId>): List<GrpcUser> {
        val request = GetAllByIdsRequest.newBuilder()
            .addAllUserIds(userIds)
            .build()
        val response = client.await().getAllByIds(request)
        return response.usersList.map { GrpcUser.fromNetwork(it) }
    }

}