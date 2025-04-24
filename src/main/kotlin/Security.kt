package com.sproutscout.api

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.auth.OAuthServerSettings
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.oauth
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    val dotenv: Dotenv by inject()
    val httpClient: HttpClient by inject()

    val jwtAudience = environment.config.property("jwt.audience").getString()
    val jwtDomain = environment.config.property("jwt.domain").getString()
    val jwtRealm = environment.config.property("jwt.realm").getString()
    val jwtSecret = dotenv["JWT_SECRET"]

    authentication {
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

            urlProvider = { "http://localhost:8080/callback" }
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
