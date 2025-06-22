package com.tometrics.api.user.domain.models

import kotlinx.serialization.Serializable

sealed class ApiException : RuntimeException()

@Serializable
data class ErrorResponse(
    val error: String,
    val errors: List<String>? = null,
)
