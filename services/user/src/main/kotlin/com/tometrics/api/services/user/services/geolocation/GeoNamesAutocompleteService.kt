package com.tometrics.api.services.user.services.geolocation

import com.tometrics.api.services.user.db.GeoNameCity500Dao
import com.tometrics.api.services.user.db.models.toLocationInfo
import com.tometrics.api.services.user.domain.models.LocationInfo
import io.ktor.server.plugins.*

class GeoNamesAutocompleteService(
    private val geoNameCity500Dao: GeoNameCity500Dao
) : GeolocationAutocompleteService {

    override suspend fun search(query: String): List<LocationInfo> {
        if (query.length < 3) throw BadRequestException("search query should be at least 3 characters long")

        // Search for cities across multiple fields (name, country, state, etc.)
        val cities = geoNameCity500Dao.search(query)

        // Convert each city entity to a LocationInfo object
        // Results are already sorted by population in descending order
        return cities.map { it.toLocationInfo() }
    }
}
