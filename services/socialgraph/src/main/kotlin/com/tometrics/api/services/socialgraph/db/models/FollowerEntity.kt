package com.tometrics.api.services.socialgraph.db.models

import com.tometrics.api.common.domain.models.UserId
import java.time.Instant

data class FollowerEntity(
    val userId: UserId,
    val followedUserId: UserId,
    val createdAt: Instant,
)