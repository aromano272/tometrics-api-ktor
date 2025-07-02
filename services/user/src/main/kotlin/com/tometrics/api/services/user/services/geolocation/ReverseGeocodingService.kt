package com.tometrics.api.services.user.services.geolocation

import com.tometrics.api.services.user.domain.models.LocationInfo


interface ReverseGeocodingService {
    suspend fun reverseGeocode(lat: Double, lon: Double): LocationInfo?
}

