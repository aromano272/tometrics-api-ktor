package com.tometrics.api.user.routes.models

import com.tometrics.api.common.domain.models.UserId
import kotlinx.serialization.Serializable

@Serializable
data class ValidateUsersRequest(
    val userIds: List<UserId>,
)