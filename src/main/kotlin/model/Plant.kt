package com.sproutscout.api.model

import kotlinx.serialization.Serializable

typealias PlantId = Int

@Serializable
data class Plant(
    val id: PlantId,
    val name: String,
    val timeToHarvest: Int,
    val bestMonths: List<Month>,
    val description: String,
    val yield: PlantYield,
    val yieldPerAreaM2: PlantYield,
    val companionPlants: List<PlantRef>,
    val plantType: String, // enum?
    val spacingFromCm: Int,
    val spacingToCm: Int,
    val sunlight: String,
    val soilNeeds: String,
    val waterNeeds: String,
    val growingTips: List<String>,
    val monthlyGuide: List<MonthlyGuide>
)

enum class YieldUnit {
    UNIT, KG, GRAMS
}

@Serializable
data class PlantYield(
    val from: Int,
    val to: Int,
    val unit: YieldUnit,
)

@Serializable
data class PlantRef(
    val id: PlantId,
    val name: String,
)

@Serializable
data class MonthlyGuide(
    val month: Month,
    val activity: String
)
