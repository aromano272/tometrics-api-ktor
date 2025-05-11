package com.tometrics.api.domain.models

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