package com.tometrics.api.services.socialfeed.routes.models

import com.tometrics.api.common.domain.models.ImageUrl
import com.tometrics.api.common.domain.models.Millis
import com.tometrics.api.common.domain.models.PostId
import com.tometrics.api.common.route.models.LocationInfoDto
import com.tometrics.api.common.route.models.UserDto
import com.tometrics.api.services.socialfeed.db.models.PostEntity
import com.tometrics.api.services.socialfeed.domain.models.Reaction
import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    val id: PostId,
    val user: UserDto,
    val locationInfoDto: LocationInfoDto?,

    val images: List<ImageUrl>,
    // TODO(aromano): implement
//    val harvests: List<Harvest>,
    val text: String,
    val myReaction: Reaction?,
    val reactionsCount: Int,
    val topReactions: List<Reaction>,
    val commentsCount: Int,

    val createdAt: Millis,
)

fun PostEntity.toDto(
    user: UserDto,
    locationInfo: LocationInfoDto?,
    myReaction: Reaction?,
    topReactions: List<Reaction>,
): PostDto = PostDto(
    id = id,
    user = user,
    locationInfoDto = locationInfo,
    images = images,
    text = text,
    myReaction = myReaction,
    reactionsCount = reactionsCount,
    topReactions = topReactions,
    commentsCount = commentsCount,
    createdAt = createdAt.toEpochMilli(),
)