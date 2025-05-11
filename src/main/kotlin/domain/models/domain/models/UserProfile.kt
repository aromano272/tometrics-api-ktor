package com.tometrics.api.domain.models.domain.models

import com.tometrics.api.domain.models.LocationInfo
import com.tometrics.api.domain.models.Millis
import com.tometrics.api.domain.models.UserId

data class UserProfile(
    val userId: UserId,
    val name: String?,
    val locationId: LocationInfo?,
    val updatedAt: Millis,
)
