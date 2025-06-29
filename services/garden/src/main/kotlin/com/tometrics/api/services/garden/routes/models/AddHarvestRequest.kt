package com.tometrics.api.services.garden.routes.models

import com.tometrics.api.common.domain.models.Millis
import com.tometrics.api.services.garden.domain.models.PlantingId
import com.tometrics.api.services.garden.domain.models.YieldUnit
import kotlinx.serialization.Serializable

@Serializable
data class AddHarvestRequest(
    val plantingId: PlantingId,
    val quantity: Float,
    val unit: YieldUnit,
    val createdAt: Millis? = null,
)