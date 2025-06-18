package com.tometrics.api.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val userId: UserId,
    val name: String?,
    val location: LocationInfo?,
    val metricUnits: Boolean,
    val climateZone: ClimateZone?,
    val updatedAt: Millis,
)