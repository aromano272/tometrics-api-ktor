package com.sproutscout.api.database.models

import com.sproutscout.api.domain.models.User

data class UserEntity(
    val id: Int,
    val name: String,
    val email: String,
    val idpGoogleEmail: String?, // Field for Google ID provider
    val anon: Boolean,
)

fun UserEntity.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    idpGoogleEmail = idpGoogleEmail, // Map Google ID provider field
    anon = anon,
)
