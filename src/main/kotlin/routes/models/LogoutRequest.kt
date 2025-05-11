package com.tometrics.api.routes.models

import kotlinx.serialization.Serializable

@Serializable
data class LogoutRequest(
    val refreshToken: String,
)