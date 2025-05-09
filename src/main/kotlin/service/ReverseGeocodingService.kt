package com.tometrics.api.service

import com.tometrics.api.domain.models.LocationInfo

interface ReverseGeocodingService {
    suspend fun reverseGeocode(lat: Double, lon: Double): LocationInfo?
}

