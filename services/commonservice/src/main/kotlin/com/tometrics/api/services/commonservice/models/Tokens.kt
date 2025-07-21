package com.tometrics.api.services.commonservice.models

import kotlinx.serialization.Serializable

@Serializable
data class Tokens(
    val access: String,
    val refresh: String,
)