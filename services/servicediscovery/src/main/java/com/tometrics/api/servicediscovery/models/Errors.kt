package com.tometrics.api.servicediscovery.models

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: String,
    val errors: List<String>? = null,
)
