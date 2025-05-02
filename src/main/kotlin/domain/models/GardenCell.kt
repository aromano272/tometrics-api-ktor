package com.tometrics.api.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class GardenCell(
    val x: Int,
    val y: Int,
    val plant: Plant?,
)

@Serializable
data class GardenCellRef(
    val x: Int,
    val y: Int,
    val plantId: PlantId?,
)

fun GardenCellRef.toDomain(plant: Plant?) = GardenCell(x, y, plant)