package com.tometrics.api.db.models

import com.tometrics.api.domain.models.Harvest
import com.tometrics.api.domain.models.HarvestId
import com.tometrics.api.domain.models.PlantingId
import com.tometrics.api.domain.models.YieldUnit
import java.time.Instant

data class HarvestEntity(
    val id: HarvestId,
    val plantingId: PlantingId,
    val quantity: Float,
    val unit: YieldUnit,
    val createdAt: Instant,
)

fun HarvestEntity.toDomain() = Harvest(
    id = id,
    plantingId = plantingId,
    quantity = quantity,
    unit = unit,
    createdAt = createdAt.toEpochMilli(),
)