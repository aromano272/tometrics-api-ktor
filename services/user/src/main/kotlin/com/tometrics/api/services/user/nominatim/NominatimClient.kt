package com.tometrics.api.services.user.nominatim

import com.tometrics.api.services.user.nominatim.models.NominatimPlace
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

interface NominatimClient {
    suspend fun reverseGeocode(lat: Double, lon: Double): NominatimPlace
}

class DefaultNominatimClient(
    private val client: HttpClient,
) : NominatimClient {
    override suspend fun reverseGeocode(lat: Double, lon: Double): NominatimPlace =
        client.get("https://nominatim.openstreetmap.org/reverse") {
            parameter("format", "jsonv2")
            parameter("lat", lat)
            parameter("lon", lon)
        }.body()
}