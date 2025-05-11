package com.tometrics.api.db.models

import com.tometrics.api.domain.models.LocationInfo
import com.tometrics.api.domain.models.UserId
import com.tometrics.api.domain.models.domain.models.UserProfile
import java.time.Instant

data class UserProfileEntity(
    val userId: UserId,
    val name: String?,
    val locationId: LocationInfoId?,
    val updatedAt: Instant,
)

fun UserProfileEntity.toDomain(location: LocationInfo?) = UserProfile(
    userId = userId,
    name = name,
    locationId = location,
    updatedAt = updatedAt.toEpochMilli(),
)
