package com.tometrics.api.services.user.db.models

import com.tometrics.api.common.domain.models.LocationInfoId
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.common.domain.models.ClimateZone
import com.tometrics.api.services.user.domain.models.LocationInfo
import com.tometrics.api.services.user.domain.models.User
import java.time.Instant

data class UserEntity(
    val id: UserId,
    val name: String,
    val idpGoogleEmail: String?,
    val idpFacebookId: String?,
    val idpFacebookEmail: String?,
    val anon: Boolean,
    val locationId: LocationInfoId?,
    val metricUnits: Boolean,
    val climateZone: ClimateZone?,
    val updatedAt: Instant,
)

fun UserEntity.toDomain(location: LocationInfo? = null): User = User(
    id = id,
    name = name,
    idpGoogleEmail = idpGoogleEmail,
    idpFacebookId = idpFacebookId,
    idpFacebookEmail = idpFacebookEmail,
    anon = anon,
    location = location,
    metricUnits = metricUnits,
    climateZone = climateZone,
    updatedAt = updatedAt,
)
