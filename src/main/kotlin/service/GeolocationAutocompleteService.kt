package com.tometrics.api.service

import com.tometrics.api.domain.models.LocationInfo

interface GeolocationAutocompleteService {
    suspend fun search(query: String): List<LocationInfo>
}
