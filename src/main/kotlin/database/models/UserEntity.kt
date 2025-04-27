package com.sproutscout.api.database.models

import com.sproutscout.api.models.IdProviderType
import com.sproutscout.api.models.User


data class UserEntity(
    val id: Int,
    val name: String,
    val email: String,
    val idProviderType: IdProviderType?,
    val anon: Boolean,
)

fun UserEntity.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    idProviderTypes = idProviderType,
    anon = anon,
)