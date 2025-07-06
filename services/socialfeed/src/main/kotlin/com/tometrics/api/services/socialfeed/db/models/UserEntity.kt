package com.tometrics.api.services.socialfeed.db.models

import com.tometrics.api.common.domain.models.ClimateZone
import com.tometrics.api.common.domain.models.LocationInfoId
import com.tometrics.api.common.domain.models.UserId
import java.time.Instant

data class UserEntity(
    val id: UserId,
    val name: String?,
    val locationId: LocationInfoId?,
    val climateZone: ClimateZone?,
    val createdAt: Instant,
    val updatedAt: Instant,
)