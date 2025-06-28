package com.tometrics.api.services.user.routes.models

import com.tometrics.api.common.domain.models.LocationInfoId
import com.tometrics.api.services.user.domain.models.ClimateZone
import kotlinx.serialization.Serializable

@Serializable
data class PutUserRequest(
    val name: String?,
    val locationId: LocationInfoId?,
    val metricUnits: Boolean?,
    val climateZone: ClimateZone?,
)