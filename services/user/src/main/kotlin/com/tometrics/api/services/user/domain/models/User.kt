package com.tometrics.api.services.user.domain.models

import com.tometrics.api.common.domain.models.Millis
import com.tometrics.api.common.domain.models.UserId
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: UserId,
    val name: String,
    val idpGoogleEmail: String?,
    val idpFacebookId: String?,
    val idpFacebookEmail: String?,
    val anon: Boolean,

    // Profile info
    val location: LocationInfo?,
    val metricUnits: Boolean,
    val climateZone: ClimateZone?,

    val updatedAt: Millis,
)
