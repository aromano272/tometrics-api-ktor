package com.sproutscout.api.domain.models

import kotlinx.serialization.Serializable

typealias UserId = Int

@Serializable
data class User(
    val id: UserId,
    val name: String,
    val email: String,
    val idpGoogleEmail: String?, // Field for Google ID provider
    val anon: Boolean,
)
