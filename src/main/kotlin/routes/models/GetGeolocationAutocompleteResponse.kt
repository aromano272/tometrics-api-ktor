package com.tometrics.api.routes.models

import com.tometrics.api.domain.models.LocationInfo
import kotlinx.serialization.Serializable

@Serializable
data class GetGeolocationAutocompleteResponse(
    val locations: List<LocationInfo>,
)