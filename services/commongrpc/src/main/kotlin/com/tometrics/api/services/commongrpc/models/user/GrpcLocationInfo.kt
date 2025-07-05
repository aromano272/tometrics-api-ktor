package com.tometrics.api.services.commongrpc.models.user

import com.tometrics.api.common.domain.models.LocationInfoId
import com.tometrics.api.services.protos.LocationInfo
import com.tometrics.api.services.protos.locationInfo

data class GrpcLocationInfo(
    val id: LocationInfoId,
    val city: String? = null,
    val country: String? = null,
    val countryCode: String? = null,
) {
    companion object {
        fun fromNetwork(network: LocationInfo): GrpcLocationInfo = GrpcLocationInfo(
            id = network.id,
            city = network.city,
            country = network.city,
            countryCode = network.countryCode,
        )
    }
}

fun GrpcLocationInfo.toNetwork(): LocationInfo = locationInfo {
    id = this@toNetwork.id
    this@toNetwork.city?.let {
        city = it
    }
    this@toNetwork.country?.let {
        country = it
    }
    this@toNetwork.countryCode?.let {
        countryCode = it
    }
}
