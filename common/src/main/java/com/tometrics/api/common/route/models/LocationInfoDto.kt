package com.tometrics.api.common.route.models

import com.tometrics.api.common.domain.models.LocationInfoId
import kotlinx.serialization.Serializable

@Serializable
data class LocationInfoDto(
    val id: LocationInfoId,
    val city: String? = null,
    val country: String? = null,
    val countryCode: String? = null,
)
