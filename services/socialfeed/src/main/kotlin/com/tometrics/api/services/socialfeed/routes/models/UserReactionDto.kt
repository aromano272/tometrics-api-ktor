package com.tometrics.api.services.socialfeed.routes.models

import com.tometrics.api.common.route.models.UserDto
import com.tometrics.api.services.socialfeed.domain.models.Reaction
import kotlinx.serialization.Serializable

@Serializable
data class UserReactionDto(
    val user: UserDto,
    val reaction: Reaction,
)