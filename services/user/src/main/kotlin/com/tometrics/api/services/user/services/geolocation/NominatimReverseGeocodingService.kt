package com.tometrics.api.services.user.services.geolocation

import com.tometrics.api.services.user.db.GeoNameCity500Dao
import com.tometrics.api.services.user.db.models.toLocationInfo
import com.tometrics.api.services.user.domain.models.LocationInfo
import com.tometrics.api.services.user.nominatim.NominatimClient
import io.ktor.util.logging.*

class NominatimReverseGeocodingService(
    private val nominatimClient: NominatimClient,
    private val geoNameCity500Dao: GeoNameCity500Dao,
    private val unmatchedPlacesLogger: Logger,
) : ReverseGeocodingService {
    override suspend fun reverseGeocode(lat: Double, lon: Double): LocationInfo? =
        nominatimClient.reverseGeocode(lat, lon)
            .let {
                val name = it.address?.city.orEmpty()
                val countryCode = it.address?.countryCode.orEmpty()
                val state = it.address?.state
                val result = geoNameCity500Dao.findByNameAndAdmin1Similarity(
                    name = name,
                    countryCode = countryCode,
                    asciiadmin1 = state,
                )
                if (result == null) {
                    unmatchedPlacesLogger.info("name: $name, country: $countryCode, state: $state, nominatimPlace: $it")
                }
                result
            }
            ?.toLocationInfo()
}
