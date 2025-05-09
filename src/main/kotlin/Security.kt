package com.tometrics.api

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.client.*
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
    }
}
