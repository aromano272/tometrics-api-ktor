package com.tometrics.api.routes

import com.tometrics.api.domain.models.BadRequestException
import com.tometrics.api.domain.models.IdProviderPayload
import com.tometrics.api.domain.models.requester
import com.tometrics.api.routes.models.AuthFacebookLoginRequest
import com.tometrics.api.routes.models.AuthGoogleLoginRequest
import com.tometrics.api.routes.models.TokensResponse
import com.tometrics.api.service.AuthService
import io.github.smiley4.ktoropenapi.get
import io.github.smiley4.ktoropenapi.post
import io.github.smiley4.ktoropenapi.route
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.authRoutes() {
    val authService: AuthService by inject()

    route("/auth", {
        tags = listOf("Auth")
    }) {

        authenticate {
            get("/verify", {
                description = "Verify if the provided authentication token is valid"
                response {
                    HttpStatusCode.OK to {
                        description = "Token is valid"
                    }
                    HttpStatusCode.Unauthorized to {
                        description = "Invalid or expired token"
                    }
                }
            }) {
                call.respond(HttpStatusCode.OK)
            }
        }

        post("/anon/register", {
            description = "Register an anonymous user"
            response {
                HttpStatusCode.OK to {
                    description = "Tokens for the newly registered anonymous user"
                    body<TokensResponse>()
                }
            }
        }) {
            val tokens = authService.registerAnon()
            call.respond(tokens)
        }

        authenticate(optional = true) {
            post("/google/login", {
                description = "Login using Google authentication"
                request {
                    body<AuthGoogleLoginRequest> {
                        description = "Google login request containing CSRF token and ID token"
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "Tokens for the authenticated user"
                        body<TokensResponse>()
                    }
                    HttpStatusCode.BadRequest to {
                        description = "Invalid CSRF token or other bad request"
                    }
                }
            }) {
                val requester = call.requester()
                val csrfCookie = call.request.cookies["g_csrf_token"]
                    ?: throw BadRequestException("No CSRF token in Cookie")
                val request = call.receive<AuthGoogleLoginRequest>()
                if (csrfCookie != request.csrfToken) {
                    throw BadRequestException("Failed to verify double submit cookie")
                }

                val tokens = authService.loginWithGoogle(requester, request.idToken)
                call.respond(tokens)
            }

            post("/facebook/login", {
                description = "Login using Facebook authentication"
                request {
                    body<AuthFacebookLoginRequest> {
                        description = "Facebook login request containing CSRF token and ID token"
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "Tokens for the authenticated user"
                        body<TokensResponse>()
                    }
                    HttpStatusCode.BadRequest to {
                        description = "Invalid CSRF token or other bad request"
                    }
                }
            }) {
                val requester = call.requester()
                val request = call.receive<AuthFacebookLoginRequest>()

                val tokens = authService.loginWithFacebook(
                    requester,
                    IdProviderPayload.Facebook(request.id, request.name, request.email)
                )
                call.respond(tokens)
            }
        }
    }
}

