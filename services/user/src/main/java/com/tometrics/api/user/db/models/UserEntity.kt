package com.tometrics.api.user.db.models

import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.user.domain.models.User

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
