package com.sproutscout.api.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val idProviderTypes: List<IdProviderType>,
    val anon: Boolean,
)