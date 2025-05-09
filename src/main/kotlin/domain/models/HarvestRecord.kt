package com.tometrics.api.domain.models

import kotlinx.serialization.Serializable

typealias HarvestRecordId = Int

@Serializable
data class HarvestRecord(
    val id: HarvestRecordId,
    val plantingId: PlantingId,
    val yield: PlantYield,
    val notes: String?,
    val createdAt: Millis,
)
