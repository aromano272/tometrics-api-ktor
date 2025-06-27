package com.tometrics.api.routes

import com.tometrics.api.domain.models.UserProfile
import com.tometrics.api.auth.domain.models.requireRequester
import com.tometrics.api.routes.models.PutUserProfileRequest
import com.tometrics.api.service.UserProfileService
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.put
import io.github.smiley4.ktoropenapi.route
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userProfileRoutes() {
    val userProfileService: UserProfileService by inject()

    authenticate {
        route("/user-profile", {
            tags = listOf("User Profile")
        }) {

            get({
                description = "Retrieves the profile of the authenticated user."
                response {
                    HttpStatusCode.OK to {
                        description = "The user's profile."
                        body<UserProfile>()
                    }
                }
            }) {
                val requester = call.requireRequester()
                val profile = userProfileService.get(requester)

                call.respond(profile)
            }

            put({
                description = "Updates the profile of the authenticated user."
                request {
                    body<PutUserProfileRequest> {
                        description = "The updated profile details."
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "The updated user profile."
                        body<UserProfile>()
                    }
                }
            }) {
                val requester = call.requireRequester()
                val request = call.receive<PutUserProfileRequest>()
                val profile = userProfileService.update(
                    requester,
                    request.name,
                    request.locationId,
                    request.metricUnits,
                    request.climateZone,
                )

                call.respond(profile)
            }

        }
    }

}