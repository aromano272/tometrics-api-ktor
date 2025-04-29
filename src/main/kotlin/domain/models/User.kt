package com.sproutscout.api.domain.models

import kotlinx.serialization.Serializable

typealias UserId = Int

@Serializable
data class User(
    val id: UserId,
    val name: String,
    val idpGoogleEmail: String?,
    val idpFacebookId: String?,
    val idpFacebookEmail: String?,
    val anon: Boolean,
)
