package com.tometrics.api.domain.models.domain.models

import com.tometrics.api.domain.models.LocationInfo
import com.tometrics.api.domain.models.Millis
import com.tometrics.api.domain.models.UserId
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val userId: UserId,
    val name: String?,
    val location: LocationInfo?,
    val updatedAt: Millis,
)
