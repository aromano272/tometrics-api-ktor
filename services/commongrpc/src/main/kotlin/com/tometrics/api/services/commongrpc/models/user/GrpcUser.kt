package com.tometrics.api.services.commongrpc.models.user

import com.tometrics.api.common.domain.models.Millis
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.protos.User
import com.tometrics.api.services.protos.locationOrNull
import com.tometrics.api.services.protos.user

data class GrpcUser(
    val id: UserId,
    val name: String,
    val location: GrpcLocationInfo?,
    val climateZone: GrpcClimateZone?,
    val updatedAt: Millis,
) {
    companion object {
        fun fromNetwork(network: User): GrpcUser = GrpcUser(
            id = network.id,
            name = network.name,
            location = network.locationOrNull?.let { GrpcLocationInfo.fromNetwork(it) },
            climateZone = GrpcClimateZone.fromNetwork(network.climateZone),
            updatedAt = network.updatedAt
        )
    }
}

fun GrpcUser.toNetwork(): User = user {
    id = this@toNetwork.id
    name = this@toNetwork.name
    this@toNetwork.location?.let {
        location = it.toNetwork()
    }
    this@toNetwork.climateZone?.let {
        climateZone = it.toNetwork()
    }
    updatedAt = this@toNetwork.updatedAt
}
