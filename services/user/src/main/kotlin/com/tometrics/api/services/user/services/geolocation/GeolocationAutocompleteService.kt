package com.tometrics.api.services.user.services.geolocation

import com.tometrics.api.services.user.domain.models.LocationInfo


interface GeolocationAutocompleteService {
    suspend fun search(query: String): List<LocationInfo>
}
