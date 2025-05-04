package com.tometrics.api.domain.models

import kotlinx.serialization.Serializable

typealias PlantingId = Int

@Serializable
data class Planting(
    val id: PlantingId,
    val plant: Plant,
    val areaSqM: Int,
    val totalYield: PlantYield,
    val quantity: Int,
    val createdAt: Millis,
    val readyToHarvestAt: Millis,
)