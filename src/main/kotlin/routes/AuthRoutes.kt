package com.sproutscout.api.routes

import com.sproutscout.api.models.BadRequestException
import com.sproutscout.api.models.requester
import com.sproutscout.api.service.AuthService
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.util.logging.Logger
import kotlinx.serialization.Serializable

fun Route.authRoutes(
    authService: AuthService,
    logger: Logger,
) {
    route("/auth") {
        post("/anon/register") {
            val tokens = authService.registerAnon()
            call.respond(tokens)
        }

        route("/google") {
            authenticate(optional = true) {
                post("/login") {
                    val requester = call.requester()
                    val csrfCookie = call.request.cookies["g_csrf_token"]
                        ?: throw BadRequestException("No CSRF token in Cookie")
                    val formParams = call.receiveParameters()
                    val request = call.receive<AuthGoogleLoginRequest>()
                    if (csrfCookie != request.csrfToken) {
                        throw BadRequestException("Failed to verify double submit cookie")
                    }

                    val tokens = authService.loginWithGoogle(requester, request.idToken)
                    call.respond(tokens)
                }
            }
        }
    }
}

@Serializable
data class AuthGoogleLoginRequest(
    val csrfToken: String,
    val idToken: String
)