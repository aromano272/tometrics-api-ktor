package com.sproutscout.api.service

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.sproutscout.api.domain.models.BadRequestException
import com.sproutscout.api.domain.models.IdProviderPayload

interface GoogleAuthService {
    suspend fun verify(idToken: String): IdProviderPayload
}

class DefaultGoogleAuthService(
    private val verifier: GoogleIdTokenVerifier,
) : GoogleAuthService {

    override suspend fun verify(idToken: String): IdProviderPayload {
        val idToken = verifier.verify(idToken)
            ?: throw BadRequestException("Invalid Id token")

        val name = idToken.payload["name"] as? String
        val email = idToken.payload.email

        return IdProviderPayload(
            name = name,
            email = email
        )
    }
}