package com.sproutscout.api.routes

import com.sproutscout.api.service.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.OAuthAccessTokenResponse
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.authRoutes(
    authService: AuthService,
) {
    route("/auth") {
        route("/google") {
            authenticate("auth-oauth-google") {
                get("/login") {
                    // Redirects to 'authorizeUrl' automatically
                }

                get("/callback") {
                    val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()

                    if (principal == null) {
                        return@get call.respond(HttpStatusCode.Unauthorized)
                    }

                    authService.handleGoogleCallback(principal)
                }
            }
        }
    }
}