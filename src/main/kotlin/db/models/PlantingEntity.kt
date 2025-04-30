package com.sproutscout.api.db.models

import com.sproutscout.api.domain.models.Planting
import com.sproutscout.api.domain.models.PlantingId
import com.sproutscout.api.domain.models.UserId
import com.sproutscout.api.model.Plant
import com.sproutscout.api.model.PlantId
import com.sproutscout.api.model.PlantYield
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