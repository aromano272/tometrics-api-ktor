package com.tometrics.api.services.user.domain.models

sealed interface IdProviderPayload {
    data class Google(
        val name: String?,
        val email: String,
    ) : IdProviderPayload

    data class Facebook(
        val id: String,
        val name: String,
        val email: String,
    ) : IdProviderPayload
}