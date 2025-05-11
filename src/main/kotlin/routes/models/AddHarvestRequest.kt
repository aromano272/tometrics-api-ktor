package com.tometrics.api.routes.models

import com.tometrics.api.domain.models.Millis
import com.tometrics.api.domain.models.PlantingId
import com.tometrics.api.domain.models.YieldUnit
import kotlinx.serialization.Serializable

@Serializable
data class AddHarvestRequest(
    val plantingId: PlantingId,
    val quantity: Float,
    val unit: YieldUnit,
    val createdAt: Millis? = null,
)