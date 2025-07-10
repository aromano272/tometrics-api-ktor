package com.tometrics.api.services.user.services.geolocation

import com.tometrics.api.common.domain.models.LocationInfoId
import com.tometrics.api.services.user.domain.models.LocationInfo


interface GeolocationAutocompleteService {
    suspend fun findLocationById(id: LocationInfoId): LocationInfo?
    suspend fun search(query: String): List<LocationInfo>
}
