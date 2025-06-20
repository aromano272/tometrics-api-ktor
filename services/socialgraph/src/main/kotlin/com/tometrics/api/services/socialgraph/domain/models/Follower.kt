package com.tometrics.api.services.socialgraph.domain.models

import com.tometrics.api.common.domain.models.Millis
import com.tometrics.api.common.domain.models.UserId
import kotlinx.serialization.Serializable

@Serializable
data class Follower(
    val userId: UserId,
    val followedUserId: UserId,
    val createdAt: Millis,
)