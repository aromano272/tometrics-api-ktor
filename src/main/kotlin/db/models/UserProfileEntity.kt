package com.tometrics.api.db.models

import com.tometrics.api.domain.models.*
import java.time.Instant

data class UserProfileEntity(
    val userId: UserId,
    val name: String?,
    val locationId: LocationInfoId?,
    val metricUnits: Boolean,
    val climateZone: ClimateZone?,
    val updatedAt: Instant,
)

fun UserProfileEntity.toDomain(location: LocationInfo?) = UserProfile(
    userId = userId,
    name = name,
    location = location,
    metricUnits = metricUnits,
    climateZone = climateZone,
    updatedAt = updatedAt.toEpochMilli(),
)
