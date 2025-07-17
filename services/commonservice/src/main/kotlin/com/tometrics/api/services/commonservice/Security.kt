package com.tometrics.api.services.commonservice

import io.github.cdimascio.dotenv.Dotenv
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    val dotenv: Dotenv by inject()
    val jwtService: JwtService by inject()

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
            realm = jwtService.jwtRealm
            verifier(jwtService.createVerifier())
            validate { credential ->
                if (credential.payload.audience.contains(jwtService.jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
}
