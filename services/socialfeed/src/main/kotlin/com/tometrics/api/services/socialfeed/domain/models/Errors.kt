package com.tometrics.api.services.socialfeed.domain.models

import com.tometrics.api.common.domain.models.CommentId
import com.tometrics.api.common.domain.models.LocationInfoId
import com.tometrics.api.common.domain.models.PostId
import com.tometrics.api.common.domain.models.UserId

sealed class ServiceError : RuntimeException()

data object CreatePostFailed : ServiceError()
data object CreateCommentFailed : ServiceError()
data class UserNotFound(val userId: UserId) : ServiceError()
data class LocationNotFound(val locationId: LocationInfoId) : ServiceError()
data class PostNotFound(val postId: PostId) : ServiceError()
data class CommentNotFound(val commentId: CommentId) : ServiceError()
data object InvalidMediaUrls : ServiceError()