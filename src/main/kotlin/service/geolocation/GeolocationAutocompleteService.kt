package com.tometrics.api.service.geolocation

import com.tometrics.api.domain.models.LocationInfo

interface GeolocationAutocompleteService {
    suspend fun search(query: String): List<LocationInfo>
}
