package com.tometrics.api.service.geolocation

import com.tometrics.api.domain.models.LocationInfo

interface ReverseGeocodingService {
    suspend fun reverseGeocode(lat: Double, lon: Double): LocationInfo?
}

