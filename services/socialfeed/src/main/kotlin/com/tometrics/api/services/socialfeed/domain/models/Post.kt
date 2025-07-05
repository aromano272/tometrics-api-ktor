package com.tometrics.api.services.socialfeed.domain.models

import com.tometrics.api.common.domain.models.ImageUrl
import com.tometrics.api.common.domain.models.Millis
import com.tometrics.api.common.route.models.LocationInfoDto
import com.tometrics.api.common.route.models.UserDto
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val user: UserDto,
    val locationInfoDto: LocationInfoDto?,

    val images: List<ImageUrl>,
    // TODO(aromano): implement
//    val harvests: List<Harvest>,
    val text: String,
    val myReaction: Reaction?,
    val reactionsCount: Int,
    val reactions: List<Reaction>,
    val commentsCount: Int,

    val createdAt: Millis,
)

