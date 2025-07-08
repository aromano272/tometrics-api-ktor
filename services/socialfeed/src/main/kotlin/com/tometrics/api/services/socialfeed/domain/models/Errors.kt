package com.tometrics.api.services.socialfeed.domain.models

import com.tometrics.api.common.domain.models.PostId

sealed class ServiceError : RuntimeException()

data object CreatePostFailed : ServiceError()
data class PostNotFound(val postId: PostId) : ServiceError()