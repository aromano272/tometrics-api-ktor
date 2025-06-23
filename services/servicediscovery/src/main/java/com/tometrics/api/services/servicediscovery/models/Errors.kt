package com.tometrics.api.services.servicediscovery.models

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: String,
    val errors: List<String>? = null,
)
