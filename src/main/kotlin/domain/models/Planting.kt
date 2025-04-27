package com.sproutscout.api.domain.models

import com.sproutscout.api.model.Plant
import com.sproutscout.api.model.PlantYield
import kotlinx.serialization.Serializable

typealias PlantingId = Int

@Serializable
data class Planting(
    val id: PlantingId,
    val plant: Plant,
    val areaSqM: Int,
    val totalYield: PlantYield,
    val createdAt: Millis,
)