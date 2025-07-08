package com.tometrics.api.services.commongrpc.services

import com.google.protobuf.Int32Value
import com.tometrics.api.common.domain.models.LocationInfoId
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.commongrpc.models.user.GrpcLocationInfo
import com.tometrics.api.services.commongrpc.models.user.GrpcUser
import com.tometrics.api.services.commongrpc.models.user.GrpcValidateUsersResult
import com.tometrics.api.services.protos.GetAllByIdsRequest
import com.tometrics.api.services.protos.UserGrpcServiceGrpcKt
import com.tometrics.api.services.protos.validateUserIdsRequest

interface UserGrpcService {
    suspend fun validateUserIds(userIds: Set<UserId>): GrpcValidateUsersResult
    suspend fun getAllByIds(userIds: Set<UserId>): List<GrpcUser>
    suspend fun findLocationById(id: LocationInfoId): GrpcLocationInfo?
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

    override suspend fun findLocationById(id: LocationInfoId): GrpcLocationInfo? {
        val request = Int32Value.of(id)
        val response = client.await().findLocationById(request)
        return GrpcLocationInfo.fromNetwork(response)
    }

}