package com.tometrics.api.services.user.services

import com.google.protobuf.Int32Value
import com.tometrics.api.common.domain.models.ClimateZone
import com.tometrics.api.services.commongrpc.ifLet
import com.tometrics.api.services.commongrpc.models.user.*
import com.tometrics.api.services.protos.*
import com.tometrics.api.services.user.domain.models.LocationInfo
import com.tometrics.api.services.user.domain.models.User
import com.tometrics.api.services.user.services.geolocation.GeolocationService


class DefaultUserGrpcService(
    private val geolocationService: GeolocationService,
    private val userService: UserService,
) : UserGrpcServiceGrpcKt.UserGrpcServiceCoroutineImplBase() {

    override suspend fun validateUserIds(request: ValidateUserIdsRequest): ValidateUserIdsResponse =
        userService.validateUserIds(request.userIdsList.toSet())
            .toGrpc()
            .toNetwork()

    override suspend fun findUserById(request: Int32Value): FindUserByIdResponse {
        val result = userService.findById(request.value)
        val proto = result?.toGrpc()?.toNetwork()
        return FindUserByIdResponse.newBuilder()
            .ifLet(proto) { setUser(it) }
            .build()
    }

    override suspend fun getAllUsersByIds(request: GetAllUsersByIdsRequest): GetAllUsersByIdsResponse {
        val result = userService.getAllByIds(request.userIdsList.toSet())
        val protos = result.map { it.toGrpc().toNetwork() }
        return GetAllUsersByIdsResponse.newBuilder()
            .addAllUsers(protos)
            .build()
    }

    override suspend fun findLocationById(request: Int32Value): FindLocationByIdResponse {
        val result = geolocationService.findLocationById(request.value)
        val proto = result?.toGrpc()?.toNetwork()
        return FindLocationByIdResponse.newBuilder()
            .ifLet(proto) { setLocation(it) }
            .build()
    }

}

private fun ValidateUsersResult.toGrpc(): GrpcValidateUsersResult = when (this) {
    ValidateUsersResult.Success -> GrpcValidateUsersResult.Success
    is ValidateUsersResult.UserIdsNotFound -> GrpcValidateUsersResult.UserIdsNotFound(missingUserIds)
}

private fun User.toGrpc(): GrpcUser = GrpcUser(
    id = id,
    name = name,
    location = location?.toGrpc(),
    climateZone = climateZone?.toGrpc(),
    updatedAt = updatedAt.toEpochMilli(),
)

private fun LocationInfo.toGrpc(): GrpcLocationInfo = GrpcLocationInfo(
    id = id,
    city = city,
    country = country,
    countryCode = countryCode,
)

private fun ClimateZone.toGrpc(): GrpcClimateZone = when (this) {
    ClimateZone.TEMPERATE -> GrpcClimateZone.TEMPERATE
    ClimateZone.MEDITERRANEAN -> GrpcClimateZone.MEDITERRANEAN
    ClimateZone.CONTINENTAL -> GrpcClimateZone.CONTINENTAL
    ClimateZone.TROPICAL -> GrpcClimateZone.TROPICAL
    ClimateZone.ARID -> GrpcClimateZone.ARID
}
