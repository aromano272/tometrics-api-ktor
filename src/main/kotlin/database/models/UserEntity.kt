package com.sproutscout.api.database.models

import com.sproutscout.api.domain.models.IdProviderType
import com.sproutscout.api.domain.models.User


data class UserEntity(
    val id: Int,
    val name: String,
    val email: String,
    val idProviderTypes: List<IdProviderType>,
    val anon: Boolean,
)

fun UserEntity.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    idProviderTypes = idProviderTypes,
    anon = anon,
)