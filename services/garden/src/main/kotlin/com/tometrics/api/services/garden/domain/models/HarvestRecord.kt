package com.tometrics.api.services.garden.domain.models

import com.tometrics.api.common.domain.models.Millis
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
