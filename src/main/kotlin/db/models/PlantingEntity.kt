package com.tometrics.api.db.models

import com.tometrics.api.domain.models.*
import java.time.Instant

data class PlantingEntity(
    val id: PlantingId? = null,
    val userId: UserId,
    val plantId: PlantId,
    val quantity: Int,
    val createdAt: Instant,
    val readyToHarvestAt: Instant,
)

fun PlantingEntity.toDomain(plant: Plant): Planting = Planting(
    id = id!!,
    plant = plant,
    areaSqM = (plant.spacing.recommendedInCm() * quantity) / 100,
    totalYield = plant.yieldPerPlant * quantity,
    createdAt = createdAt.toEpochMilli(),
    readyToHarvestAt = readyToHarvestAt.toEpochMilli(),
)