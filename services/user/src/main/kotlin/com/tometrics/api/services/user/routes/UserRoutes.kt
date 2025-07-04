package com.tometrics.api.services.user.routes

import com.tometrics.api.auth.domain.models.requireRequester
import com.tometrics.api.common.domain.models.ValidationError
import com.tometrics.api.services.user.domain.models.User
import com.tometrics.api.services.user.domain.models.UserWithSocialConnections
import com.tometrics.api.services.user.routes.models.PutUserRequest
import com.tometrics.api.services.user.services.UserService
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.put
import io.github.smiley4.ktoropenapi.route
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userRoutes() {
    val userService: UserService by inject()

    authenticate {
        route("/user", {
            tags = listOf("User")
        }) {

            get({
                description = "Retrieves the profile of the authenticated user."
                response {
                    HttpStatusCode.OK to {
                        description = "The user's profile."
                        body<UserWithSocialConnections>()
                    }
                }
            }) {
                val requester = call.requireRequester()
                val profile = userService.get(requester)

                call.respond(profile)
            }

            get("/{userId}", {
                description = "Retrieves the profile of a user."
                response {
                    HttpStatusCode.OK to {
                        description = "The user's profile."
                        body<UserWithSocialConnections>()
                    }
                }
            }) {
                val userId = call.pathParameters["userId"]?.toIntOrNull() ?: throw ValidationError(listOf(
                    "`userId` is required"
                ))
                val profile = userService.get(userId)

                call.respond(profile)
            }

            put({
                description = "Updates the profile of the authenticated user."
                request {
                    body<PutUserRequest> {
                        description = "The updated profile details."
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "The updated user profile."
                        body<User>()
                    }
                }
            }) {
                val requester = call.requireRequester()
                val request = call.receive<PutUserRequest>()
                val profile = userService.update(
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
