package com.tometrics.api.services.user.routes.models

import com.tometrics.api.services.user.domain.models.LocationInfo
import kotlinx.serialization.Serializable

@Serializable
data class GetGeolocationAutocompleteResponse(
    val locations: List<LocationInfo>,
)