package com.tometrics.api.services.user.routes

import com.tometrics.api.auth.domain.models.requireRequester
import com.tometrics.api.common.domain.models.ValidationError
import com.tometrics.api.common.route.models.UserDto
import com.tometrics.api.services.user.domain.models.User
import com.tometrics.api.services.user.domain.models.toDto
import com.tometrics.api.services.user.routes.models.PutUserRequest
import com.tometrics.api.services.user.routes.models.UserWithSocialConnectionsDto
import com.tometrics.api.services.user.routes.models.toDto
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
                        body<UserWithSocialConnectionsDto>()
                    }
                }
            }) {
                val requester = call.requireRequester()
                val profile = userService.get(requester).toDto()

                call.respond(profile)
            }

            get("/{userId}", {
                description = "Retrieves the profile of a user."
                response {
                    HttpStatusCode.OK to {
                        description = "The user's profile."
                        body<UserWithSocialConnectionsDto>()
                    }
                }
            }) {
                val userId = call.pathParameters["userId"]?.toIntOrNull() ?: throw ValidationError(listOf(
                    "`userId` is required"
                ))
                val profile = userService.get(userId).toDto()

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
                        body<UserDto>()
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
                ).toDto()

                call.respond(profile)
            }

        }
    }

}

fun User.toDto() = UserDto(
    id = id,
    name = name,
    // TODO(aromano): we'll probably need to split UserDto into UserDto and UserProfileDto, the latter
    // being the requester user profile with all of the info such as idp statuses, complete location info, etc..
    location = location?.toDto(),
    climateZone = climateZone,
    updatedAt = updatedAt.toEpochMilli(),
)

