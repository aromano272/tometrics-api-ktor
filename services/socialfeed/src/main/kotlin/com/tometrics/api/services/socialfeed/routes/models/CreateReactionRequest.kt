package com.tometrics.api.services.socialfeed.routes.models

import com.tometrics.api.services.socialfeed.domain.models.Reaction
import kotlinx.serialization.Serializable

@Serializable
data class CreateReactionRequest(
    val reaction: Reaction,
)
