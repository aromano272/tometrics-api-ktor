package com.sproutscout.api.routes.models

import kotlinx.serialization.Serializable

@Serializable
data class TokensResponse(
    val accessToken: String,
    val refreshToken: String
)