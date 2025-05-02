package com.tometrics.api.db.models

import com.tometrics.api.domain.models.User
import com.tometrics.api.domain.models.UserId

data class UserEntity(
    val id: UserId,
    val name: String,
    val idpGoogleEmail: String?,
    val idpFacebookId: String?,
    val idpFacebookEmail: String?,
    val anon: Boolean,
)

fun UserEntity.toDomain(): User = User(
    id = id,
    name = name,
    idpGoogleEmail = idpGoogleEmail,
    idpFacebookId = idpFacebookId,
    idpFacebookEmail = idpFacebookEmail,
    anon = anon,
)
