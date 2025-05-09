package com.tometrics.api.domain.models

import kotlinx.serialization.Serializable

typealias LocationInfoId = Int

// TODO: align better with cities500
@Serializable
data class LocationInfo(
    val id: LocationInfoId,
    val lat: Double,
    val lon: Double,
    val city: String? = null,
    val stateDistrict: String? = null,
    val admin1: String? = null,
    val country: String? = null,
    val countryCode: String? = null,
    val continent: String? = null,
    val region: String? = null,
    val admin2: String? = null,
    val municipality: String? = null,
    val town: String? = null,
    val village: String? = null,
    val cityDistrict: String? = null,
    val district: String? = null,
)
