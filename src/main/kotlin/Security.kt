package com.tometrics.api

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    val dotenv: Dotenv by inject()
    val httpClient: HttpClient by inject()

    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtDomain = environment.config.property("jwt.domain").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()
    val jwtSecret = dotenv["JWT_SECRET"]

    authentication {
        bearer("auth-cronjob") {
            realm = "Access to the '/api/v1/cronjob' path"
            authenticate { tokenCredential ->
                if (tokenCredential.token == dotenv["CRONJOB_BEARER_TOKEN"]) {
                    Unit
                } else {
                    null
                }
            }
        }

        jwt {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }

        oauth("auth-oauth-google") {
            val redirects = mutableMapOf<String, String>()

            urlProvider = { "http://localhost:8080/api/v1/auth/google/callback" }
            providerLookup = {
                OAuthServerSettings.OAuth2ServerSettings(
                    name = "google",
                    authorizeUrl = "https://accounts.google.com/o/oauth2/auth",
                    accessTokenUrl = "https://accounts.google.com/o/oauth2/token",
                    requestMethod = HttpMethod.Post,
                    clientId = dotenv["GOOGLE_OAUTH_CLIENT_ID"],
                    clientSecret = dotenv["GOOGLE_OAUTH_CLIENT_SECRET"],
                    defaultScopes = listOf("https://www.googleapis.com/auth/userinfo.profile"),
//                    extraAuthParameters = listOf("access_type" to "offline"),
                    onStateCreated = { call, state ->
                        //saves new state with redirect url value
                        call.request.queryParameters["redirectUrl"]?.let {
                            redirects[state] = it
                        }
                    }
                )
            }
            client = httpClient
        }
    }
}
