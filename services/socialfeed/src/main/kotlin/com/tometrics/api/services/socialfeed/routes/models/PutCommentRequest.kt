package com.tometrics.api.services.socialfeed.routes.models

import com.tometrics.api.common.domain.models.CommentId
import com.tometrics.api.common.domain.models.ImageUrl
import kotlinx.serialization.Serializable

@Serializable
data class PutCommentRequest(
    val parent: CommentId?,
    val text: String?,
    val image: ImageUrl?,
)