package com.sproutscout.api.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val idProviderType: IdProviderType?,
    val anon: Boolean,
)