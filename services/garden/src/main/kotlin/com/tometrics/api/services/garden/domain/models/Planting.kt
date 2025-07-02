package com.tometrics.api.services.garden.domain.models

import com.tometrics.api.common.domain.models.Millis
import kotlinx.serialization.Serializable

typealias PlantingId = Int

@Serializable
data class Planting(
    val id: PlantingId,
    val name: String,
    val plant: Plant,
    val areaSqM: Int,
    val totalYield: PlantYield,
    val quantity: Int,
    val createdAt: Millis,
    val readyToHarvestAt: Millis,
    val diary: String,
    val harvested: Boolean,
)
