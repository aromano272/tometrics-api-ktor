package com.sproutscout.api.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Tokens(
    val access: String,
    val refresh: String,
)