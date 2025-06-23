package com.tometrics.api.services.user.routes.models

import com.tometrics.api.common.domain.models.UserId
import kotlinx.serialization.Serializable

@Serializable
data class ValidateUsersRequest(
    val userIds: List<UserId>,
)