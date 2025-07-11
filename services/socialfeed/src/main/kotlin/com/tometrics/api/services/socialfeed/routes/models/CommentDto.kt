package com.tometrics.api.services.socialfeed.routes.models

import com.tometrics.api.common.domain.models.CommentId
import com.tometrics.api.common.domain.models.ImageUrl
import com.tometrics.api.common.domain.models.Millis
import com.tometrics.api.common.route.models.UserDto
import com.tometrics.api.services.socialfeed.db.models.CommentEntity
import com.tometrics.api.services.socialfeed.domain.models.Reaction
import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(
    val user: UserDto,
    val parentId: CommentId?,
    val text: String,
    val image: ImageUrl?,
    val myReaction: Reaction?,
    val reactionsCount: Int,
    val reactions: List<Reaction>,

    val createdAt: Millis,
)

fun CommentEntity.toDto(
    user: UserDto,
    myReaction: Reaction?,
    topReactions: List<Reaction>,
): CommentDto = CommentDto(
    user = user,
    parentId = parentId,
    text = text,
    image = image,
    myReaction = myReaction,
    reactionsCount = reactionsCount,
    reactions = topReactions,
    createdAt = createdAt.toEpochMilli(),
)