package com.sproutscout.api.routes

import com.sproutscout.api.models.BadRequestException
import com.sproutscout.api.service.AuthService
import io.ktor.http.Cookie
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.util.logging.Logger

fun Route.authRoutes(
    authService: AuthService,
    logger: Logger,
) {
    get("/bla") {
        val test = call.request.cookies["test"]
        call.respondText("v2 $test")
    }
    route("/auth") {
        post("/anon/register") {
            val tokens = authService.registerAnon()
            call.respond(tokens)
        }

        route("/google") {
            authenticate(optional = true) {
                get("/initiate") {
                    val principal = call.principal<JWTPrincipal>()

                    if (principal != null) {
                        principal.payload.toString()

                    }
                    call.response.cookies.append(
                        Cookie("test", "testvalue", httpOnly = true, path="/")
                    )

                    call.respondRedirect("/api/v1/auth/google/login")
                }
            }

            post("/login") {
                val csrfCookie = call.request.cookies["g_csrf_token"]
                    ?: throw BadRequestException("No CSRF token in Cookie")
                val formParams = call.receiveParameters()
                val csrfBody = formParams["g_csrf_token"]
                    ?: throw BadRequestException("No CSRF token in Body")
                if (csrfCookie != csrfBody) {
                    throw BadRequestException("Failed to verify double submit cookie")
                }
                val credential = formParams["credential"]
                    ?: throw BadRequestException("No Id token in Body")

                val tokens = authService.loginWithGoogle(credential)
                call.respond(tokens)
            }

//            authenticate("auth-oauth-google") {
//                get("/login") {
//                    // Redirects to 'authorizeUrl' automatically
//                }
//
//                get("/callback") {
//                    val principal: OAuthAccessTokenResponse.OAuth2? = call.principal()
//
//                    if (principal == null) {
//                        return@get call.respond(HttpStatusCode.Unauthorized)
//                    }
//
//                    val tokens = authService.handleGoogleCallback(principal)
//                    call.respondRedirect()
//                }
//            }
        }
    }
}