package com.tometrics.api.db.models

import com.tometrics.api.domain.models.*
import java.time.Instant

data class PlantingEntity(
    val id: PlantingId? = null,
    val userId: UserId,
    val plantId: PlantId,
    val name: String? = null,
    val quantity: Int,
    val createdAt: Instant,
    val readyToHarvestAt: Instant,
    val diary: String = "",
    val harvested: Boolean = false,
)

fun PlantingEntity.toDomain(plant: Plant): Planting = Planting(
    id = id!!,
    name = name ?: plant.name,
    plant = plant,
    areaSqM = (plant.spacing.recommendedInCm() * quantity) / 100,
    totalYield = plant.yieldPerPlant * quantity,
    quantity = quantity,
    createdAt = createdAt.toEpochMilli(),
    readyToHarvestAt = readyToHarvestAt.toEpochMilli(),
    diary = diary,
    harvested = harvested,
)
