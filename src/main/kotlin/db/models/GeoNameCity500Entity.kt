package com.tometrics.api.db.models

import com.tometrics.api.domain.models.LocationInfo
import com.tometrics.api.domain.models.LocationInfoId
import java.util.*

// https://download.geonames.org/export/dump/readme.txt
// all cities with a population > 500 or seats of adm div down to
// PPLA4 (ca 185.000), see 'geoname' table for columns
data class GeoNameCity500Entity(
    val geonameid: LocationInfoId? = null,                // integer id of record in geonames database
    val name: String,                          // name of geographical point (utf8)
    val asciiname: String,                     // name of geographical point in plain ascii characters
    val alternatenames: String?,                // alternatenames, comma separated
    val latitude: Double,                      // latitude in decimal degrees (wgs84)
    val longitude: Double,                     // longitude in decimal degrees (wgs84)
    val featureClass: Char,                    // see http://www.geonames.org/export/codes.html
    val featureCode: String,                   // see http://www.geonames.org/export/codes.html
    val country: String,                       // Country name
    val countryCode: String,                   // ISO-3166 2-letter country code
    val cc2: String?,                           // alternate country codes, comma separated, ISO-3166 2-letter country code
    val admin1: String?,                        // second administrative division
    val asciiadmin1: String?,                   // second administrative division
    val admin2: String?,                        // second administrative division
    val asciiadmin2: String?,                   // second administrative division
    val admin3Code: String?,                    // code for third level administrative division
    val admin4Code: String?,                    // code for fourth level administrative division
    val population: Long,                      // bigint (8 byte int)
    val elevation: Int? = null,                // in meters, integer
    val dem: Int? = null,                      // digital elevation model, integer
    val timezone: String,                      // the iana timezone id
    val modificationDate: String               // date of last modification in yyyy-MM-dd format
)

fun GeoNameCity500Entity.toLocationInfo(
    requesterLocaleIso2: String? = null,
): LocationInfo = LocationInfo(
    id = geonameid!!,
    lat = latitude,
    lon = longitude,
    city = name,
    country = Locale(requesterLocaleIso2.orEmpty(), countryCode).displayCountry,
    countryCode = countryCode,
    // Map other fields as needed based on feature class/code
    // For now, we're using a simple mapping
    admin1 = admin1?.takeIf { it.isNotBlank() },
    admin2 = admin2?.takeIf { it.isNotBlank() },
)
