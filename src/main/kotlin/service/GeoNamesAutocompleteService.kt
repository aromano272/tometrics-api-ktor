package com.tometrics.api.service

import com.tometrics.api.db.GeoNameCity500Dao
import com.tometrics.api.db.models.toLocationInfo
import com.tometrics.api.domain.models.LocationInfo

class GeoNamesAutocompleteService(
    private val geoNameCity500Dao: GeoNameCity500Dao
) : GeolocationAutocompleteService {

    override suspend fun search(query: String): List<LocationInfo> {
        // Search for cities across multiple fields (name, country, state, etc.)
        val cities = geoNameCity500Dao.search(query)

        // Convert each city entity to a LocationInfo object
        // Results are already sorted by population in descending order
        return cities.map { it.toLocationInfo() }
    }
}
