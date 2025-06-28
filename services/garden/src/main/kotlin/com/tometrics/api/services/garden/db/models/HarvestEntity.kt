package com.tometrics.api.services.garden.db.models

import com.tometrics.api.services.garden.domain.models.Harvest
import com.tometrics.api.services.garden.domain.models.HarvestId
import com.tometrics.api.services.garden.domain.models.PlantingId
import com.tometrics.api.services.garden.domain.models.YieldUnit
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