package com.tometrics.api.common.route.models

import com.tometrics.api.common.domain.models.ClimateZone
import com.tometrics.api.common.domain.models.Millis
import com.tometrics.api.common.domain.models.UserId
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: UserId,
    val name: String,
    val location: LocationInfoDto?,
    val climateZone: ClimateZone?,
    val updatedAt: Millis,
)
