package com.tometrics.api.services.user.domain.models

import com.tometrics.api.common.domain.models.LocationInfoId
import com.tometrics.api.common.route.models.LocationInfoDto

// TODO: align better with cities500
data class LocationInfo(
    val id: LocationInfoId,
    val lat: Double,
    val lon: Double,
    val city: String? = null,
    val stateDistrict: String? = null,
    val admin1: String? = null,
    val country: String? = null,
    val countryCode: String? = null,
    val continent: String? = null,
    val region: String? = null,
    val admin2: String? = null,
    val municipality: String? = null,
    val town: String? = null,
    val village: String? = null,
    val cityDistrict: String? = null,
    val district: String? = null,
)

fun LocationInfo.toDto() = LocationInfoDto(
    id = id,
    city = city,
    country = country,
    countryCode = countryCode,
)
