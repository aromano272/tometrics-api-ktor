package com.tometrics.api.db.models

import com.tometrics.api.domain.models.Plant
import com.tometrics.api.domain.models.PlantId
import com.tometrics.api.domain.models.PlantYield
import com.tometrics.api.domain.models.Planting
import com.tometrics.api.domain.models.PlantingId
import com.tometrics.api.domain.models.UserId
import java.time.Instant

data class PlantingEntity(
    val id: PlantingId,
    val userId: UserId,
    val plantId: PlantId,
    val quantity: Int,
    val createdAt: Instant,
)

fun PlantingEntity.toDomain(plant: Plant): Planting = Planting(
    id = id,
    plant = plant,
    // todo
    areaSqM = 1,
    totalYield = PlantYield(
        from = plant.yieldPerPlant.from * quantity,
        to = plant.yieldPerPlant.to * quantity,
        unit = plant.yieldPerPlant.unit,
    ),
    createdAt = createdAt.toEpochMilli()
)