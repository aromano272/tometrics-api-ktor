package com.tometrics.api.services.socialfeed.domain.models

import com.tometrics.api.common.domain.models.UserId
import kotlinx.serialization.Serializable

@Serializable
data class SocialConnections(
    val followers: List<UserId>,
    val following: List<UserId>,
)