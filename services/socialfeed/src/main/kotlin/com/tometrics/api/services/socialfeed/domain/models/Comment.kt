package com.tometrics.api.services.socialfeed.domain.models

import com.tometrics.api.common.domain.models.ImageUrl
import com.tometrics.api.common.domain.models.Millis
import com.tometrics.api.common.route.models.UserDto
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val user: UserDto,
    val parent: Comment?,
    val text: String,
    val image: ImageUrl?,
    val myReaction: Reaction?,
    val reactionsCount: Int,
    val reactions: List<Reaction>,

    val createdAt: Millis,
)