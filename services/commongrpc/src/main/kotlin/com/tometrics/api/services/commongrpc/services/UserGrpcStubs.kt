package com.tometrics.api.services.commongrpc.services

import com.google.protobuf.Int32Value
import com.tometrics.api.common.domain.models.LocationInfoId
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.commongrpc.models.user.GrpcLocationInfo
import com.tometrics.api.services.commongrpc.models.user.GrpcUser
import com.tometrics.api.services.commongrpc.models.user.GrpcValidateUsersResult
import com.tometrics.api.services.protos.*

interface UserGrpcClient {
    suspend fun validateUserIds(userIds: Set<UserId>): GrpcValidateUsersResult
    suspend fun findUserById(id: UserId): GrpcUser?
    suspend fun getAllUsersByIds(userIds: Set<UserId>): List<GrpcUser>
    suspend fun findLocationById(id: LocationInfoId): GrpcLocationInfo?
}

class DefaultUserGrpcClient(
    private val client: GrpcLazyClient<UserGrpcServiceGrpcKt.UserGrpcServiceCoroutineStub>,
) : UserGrpcClient {

    override suspend fun validateUserIds(userIds: Set<UserId>): GrpcValidateUsersResult {
        val response = client.await().validateUserIds(validateUserIdsRequest {
            this.userIds += userIds
        })
        return GrpcValidateUsersResult.fromNetwork(response)
    }

    override suspend fun findUserById(id: UserId): GrpcUser? {
        val request = Int32Value.of(id)
        val response = client.await().findUserById(request)
        return response.userOrNull?.let { GrpcUser.fromNetwork(it) }
    }

    override suspend fun getAllUsersByIds(userIds: Set<UserId>): List<GrpcUser> {
        val request = GetAllUsersByIdsRequest.newBuilder()
            .addAllUserIds(userIds)
            .build()
        val response = client.await().getAllUsersByIds(request)
        return response.usersList.map { GrpcUser.fromNetwork(it) }
    }

    override suspend fun findLocationById(id: LocationInfoId): GrpcLocationInfo? {
        val request = Int32Value.of(id)
        val response = client.await().findLocationById(request)
        return response.locationOrNull?.let { GrpcLocationInfo.fromNetwork(it) }
    }

}