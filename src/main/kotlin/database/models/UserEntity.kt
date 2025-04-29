package com.sproutscout.api.database.models

import com.sproutscout.api.domain.models.User

data class UserEntity(
    val id: Int,
    val name: String,
    val email: String,
    val idpGoogleEmail: String?,
    val idpFacebookId: String?,
    val idpFacebookEmail: String?,
    val anon: Boolean,
)

fun UserEntity.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    idpGoogleEmail = idpGoogleEmail,
    idpFacebookId = idpFacebookId,
    idpFacebookEmail = idpFacebookEmail,
    anon = anon,
)
