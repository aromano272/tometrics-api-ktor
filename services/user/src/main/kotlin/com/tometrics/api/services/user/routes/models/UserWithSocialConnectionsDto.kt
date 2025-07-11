package com.tometrics.api.services.user.routes.models

import com.tometrics.api.common.route.models.UserDto
import com.tometrics.api.services.user.domain.models.UserWithSocialConnections
import com.tometrics.api.services.user.routes.toDto
import kotlinx.serialization.Serializable

@Serializable
data class UserWithSocialConnectionsDto(
    val user: UserDto,
    val followers: List<UserDto>,
    val following: List<UserDto>,
)

fun UserWithSocialConnections.toDto() = UserWithSocialConnectionsDto(
    user = user.toDto(),
    followers = followers.map { it.toDto() },
    following = following.map { it.toDto() },
)
