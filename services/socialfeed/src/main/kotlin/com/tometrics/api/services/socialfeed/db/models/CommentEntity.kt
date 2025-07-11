package com.tometrics.api.services.socialfeed.db.models

import com.tometrics.api.common.domain.models.CommentId
import com.tometrics.api.common.domain.models.UserId
import java.time.Instant

data class CommentEntity(
    val id: CommentId,
    val userId: UserId,
    val parentId: CommentId?,
    val text: String,
    val image: String?,
    val reactionsCount: Int,
    val createdAt: Instant,
    val updatedAt: Instant,
)