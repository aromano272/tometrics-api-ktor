package com.tometrics.api.services.socialfeed.db.models

import com.tometrics.api.common.domain.models.LocationInfoId
import java.time.Instant

data class LocationInfoEntity(
    val locationId: LocationInfoId,
    val city: String?,
    val country: String?,
    val countryCode: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
)