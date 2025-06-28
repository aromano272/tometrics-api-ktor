package com.tometrics.api.services.garden.domain.models

import com.tometrics.api.common.domain.models.Millis
import kotlinx.serialization.Serializable

typealias HarvestId = Int

@Serializable
data class Harvest(
    val id: HarvestId,
    val plantingId: PlantingId,
    val quantity: Float,
    val unit: YieldUnit,
    val createdAt: Millis,
)