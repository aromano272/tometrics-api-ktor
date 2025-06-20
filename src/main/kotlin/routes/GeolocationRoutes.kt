package com.tometrics.api.routes

import com.tometrics.api.domain.models.BadRequestException
import com.tometrics.api.domain.models.LocationInfo
import com.tometrics.api.domain.models.NotFoundException
import com.tometrics.api.auth.domain.models.requireRequester
import com.tometrics.api.routes.models.GetGeolocationAutocompleteResponse
import com.tometrics.api.service.geolocation.GeolocationService
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.route
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.geolocationRoutes() {
    val geolocationService: GeolocationService by inject()

    authenticate {
        route("/geolocation", {
            tags = listOf("Geolocation")
        }) {

            get("/autocomplete", {
                description = "Search for locations based on a query string."
                request {
                    queryParameter<String>("searchQuery") {
                        description = "The search query string to find matching locations."
                        required = true
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "A list of matching locations."
                        body<GetGeolocationAutocompleteResponse>()
                    }
                }
            }) {
                val requester = call.requireRequester()
                val searchQuery = call.request.queryParameters["searchQuery"].orEmpty()

                val locations = geolocationService.search(searchQuery)
                call.respond(GetGeolocationAutocompleteResponse(locations))
            }

            get("/reverse-geocoding", {
                description = "Find location details based on latitude and longitude."
                request {
                    queryParameter<Double>("lat") {
                        description = "The latitude of the location."
                        required = true
                    }
                    queryParameter<Double>("lon") {
                        description = "The longitude of the location."
                        required = true
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "The location details for the given coordinates."
                        body<LocationInfo>()
                    }
                    HttpStatusCode.BadRequest to {
                        description = "Invalid or missing query parameters."
                    }
                    HttpStatusCode.NotFound to {
                        description = "No location found for the given coordinates."
                    }
                }
            }) {
                val requester = call.requireRequester()
                val lat = call.request.queryParameters["lat"]?.toDoubleOrNull()
                    ?: throw BadRequestException("lat query param missing or invalid")
                val lon = call.request.queryParameters["lon"]?.toDoubleOrNull()
                    ?: throw BadRequestException("lon query param missing or invalid")

                val location = geolocationService.reverseGeocode(lat, lon)
                    ?: throw NotFoundException("location with lat: $lat lon: $lon not found")
                call.respond(location)
            }

        }
    }
}
