package com.tometrics.api.services.socialfeed.db.models

import com.tometrics.api.common.domain.models.PostId
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.socialfeed.domain.models.Reaction
import java.time.Instant

data class PostReactionEntity(
    val postId: PostId,
    val userId: UserId,
    val reaction: Reaction,
    val createdAt: Instant,
    val updatedAt: Instant,
)