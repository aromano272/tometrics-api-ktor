package com.tometrics.api.routes.models

import com.tometrics.api.domain.models.ClimateZone
import com.tometrics.api.domain.models.LocationInfoId
import kotlinx.serialization.Serializable

@Serializable
data class PutUserProfileRequest(
    val name: String?,
    val locationId: LocationInfoId?,
    val metricUnits: Boolean?,
    val climateZone: ClimateZone?,
)