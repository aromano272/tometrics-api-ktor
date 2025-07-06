package com.tometrics.api.services.socialfeed.db.models

import com.tometrics.api.common.domain.models.ImageUrl
import com.tometrics.api.common.domain.models.PostId
import com.tometrics.api.common.domain.models.UserId
import java.time.Instant

data class PostEntity(
    val id: PostId,
    val userId: UserId,
    val images: List<ImageUrl>,
    val text: String,
    val reactionsCount: Int,
    val commentsCount: Int,
    val createdAt: Instant,
    val updatedAt: Instant,
)