package com.tometrics.api.service

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.tometrics.api.domain.models.BadRequestException
import com.tometrics.api.domain.models.IdProviderPayload

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