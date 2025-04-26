package com.sproutscout.api.service

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.sproutscout.api.models.BadRequestException
import com.sproutscout.api.models.IdProviderPayload

interface GoogleAuthService {
    suspend fun verify(credential: String): IdProviderPayload
}

class DefaultGoogleAuthService(
    private val verifier: GoogleIdTokenVerifier,
) : GoogleAuthService {

    override suspend fun verify(credential: String): IdProviderPayload {
        val idToken = verifier.verify(credential)
            ?: throw BadRequestException("Invalid Id token")

        val name = idToken.payload["name"] as? String
        val email = idToken.payload.email

        return IdProviderPayload(
            name = name,
            email = email
        )
    }
}