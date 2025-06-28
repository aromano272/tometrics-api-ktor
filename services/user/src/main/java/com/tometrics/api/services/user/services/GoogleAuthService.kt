package com.tometrics.api.services.user.services

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.tometrics.api.services.user.domain.models.IdProviderPayload
import io.ktor.server.plugins.BadRequestException

interface GoogleAuthService {
    suspend fun verify(idToken: String): IdProviderPayload.Google
}

class DefaultGoogleAuthService(
    private val verifier: GoogleIdTokenVerifier,
) : GoogleAuthService {

    override suspend fun verify(idToken: String): IdProviderPayload.Google {
        val idToken = verifier.verify(idToken)
            ?: throw BadRequestException("Invalid Id token")

        val name = idToken.payload["name"] as? String
        val email = idToken.payload.email

        return IdProviderPayload.Google(
            name = name,
            email = email
        )
    }
}