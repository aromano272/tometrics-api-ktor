package com.sproutscout.api.routes.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthFacebookLoginRequest(
    val id: String,
    val name: String,
    val email: String,
)