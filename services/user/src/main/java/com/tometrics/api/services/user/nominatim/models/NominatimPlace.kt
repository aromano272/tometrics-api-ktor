package com.tometrics.api.services.user.nominatim.models

import com.tometrics.api.services.user.domain.models.LocationInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class NominatimPlace(
    @SerialName("place_id")
    val placeId: Long? = null,
    val licence: String? = null,
    @SerialName("osm_type")
    val osmType: String? = null,
    @SerialName("osm_id")
    val osmId: String? = null,
    val boundingbox: List<String>? = null,
    val lat: String? = null,
    val lon: String? = null,
    @SerialName("display_name")
    val displayName: String? = null,
    val category: String? = null, // "class" in JSON, renamed to category in JSONv2
    val type: String? = null,
    @SerialName("place_rank")
    val placeRank: Int? = null, // Only in JSONv2
    val importance: Double? = null,
    val icon: String? = null,
    val address: Address? = null,
    val extratags: Map<String, String>? = null,
    val namedetails: Map<String, String>? = null
) {
    // https://nominatim.org/release-docs/latest/api/Output/#place_id-is-not-a-persistent-id
    // If you need an ID that is consistent over multiple installations of Nominatim,
    // then you should use the combination of osm_type+osm_id+class.
    val uuid: String = "$osmType $osmId $category"
}

fun NominatimPlace.toLocationInfo(
    requesterLocaleIso2: String? = null,
): LocationInfo = LocationInfo(
    id = placeId?.toInt() ?: osmId?.hashCode() ?: uuid.hashCode(),
    lat = lat?.toDoubleOrNull() ?: 0.0,
    lon = lon?.toDoubleOrNull() ?: 0.0,
    city = address?.city,
    stateDistrict = address?.stateDistrict,
    admin1 = address?.state,
    country = address?.countryCode?.let { code ->
        Locale(requesterLocaleIso2.orEmpty(), code).displayCountry
    } ?: address?.country,
    countryCode = address?.countryCode?.uppercase(),
    continent = address?.continent,
    region = address?.region,
    admin2 = address?.county,
    municipality = address?.municipality,
    town = address?.town,
    village = address?.village,
    cityDistrict = address?.cityDistrict,
    district = address?.district
)

@Serializable
data class Address(
    val city: String? = null,
    @SerialName("state_district")
    val stateDistrict: String? = null,
    val state: String? = null,
    @SerialName("ISO3166-2-lvl4")
    val iso31662lvl4: String? = null,
    val postcode: String? = null,
    val country: String? = null,
    @SerialName("country_code")
    val countryCode: String? = null,
    // Additional address fields that might be present
    val continent: String? = null,
    val region: String? = null,
    val county: String? = null,
    val municipality: String? = null,
    val town: String? = null,
    val village: String? = null,
    @SerialName("city_district")
    val cityDistrict: String? = null,
    val district: String? = null,
    val borough: String? = null,
    val suburb: String? = null,
    val subdivision: String? = null,
    val hamlet: String? = null,
    val croft: String? = null,
    @SerialName("isolated_dwelling")
    val isolatedDwelling: String? = null,
    val neighbourhood: String? = null,
    val allotments: String? = null,
    val quarter: String? = null,
    @SerialName("city_block")
    val cityBlock: String? = null,
    val residential: String? = null,
    val farm: String? = null,
    val farmyard: String? = null,
    val industrial: String? = null,
    val commercial: String? = null,
    val retail: String? = null,
    val road: String? = null,
    @SerialName("house_number")
    val houseNumber: String? = null,
    @SerialName("house_name")
    val houseName: String? = null,
    val emergency: String? = null,
    val historic: String? = null,
    val military: String? = null,
    val natural: String? = null,
    val landuse: String? = null,
    val place: String? = null,
    val railway: String? = null,
    @SerialName("man_made")
    val manMade: String? = null,
    val aerialway: String? = null,
    val boundary: String? = null,
    val amenity: String? = null,
    val aeroway: String? = null,
    val club: String? = null,
    val craft: String? = null,
    val leisure: String? = null,
    val office: String? = null,
    @SerialName("mountain_pass")
    val mountainPass: String? = null,
    val shop: String? = null,
    val tourism: String? = null,
    val bridge: String? = null,
    val tunnel: String? = null,
    val waterway: String? = null
)
