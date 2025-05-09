package com.tometrics.api.service

import com.tometrics.api.domain.models.LocationInfo
import com.tometrics.api.external.nominatim.NominatimClient
import com.tometrics.api.external.nominatim.models.toLocationInfo

class NominatimReverseGeocodingService(
    private val nominatimClient: NominatimClient,
) : ReverseGeocodingService {
    override suspend fun reverseGeocode(lat: Double, lon: Double): LocationInfo? =
        nominatimClient.reverseGeocode(lat, lon).toLocationInfo()
}
