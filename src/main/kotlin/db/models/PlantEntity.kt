package com.tometrics.api.db.models

import com.tometrics.api.model.*

data class PlantEntity(
    val id: Int? = null,
    val name: String,
    val timeToHarvest: Int,
    val yieldPerPlantFrom: Float,
    val yieldPerPlantTo: Float,
    val yieldPerPlantUnit: YieldUnit,
    val yieldPerSqMFrom: Float,
    val yieldPerSqMTo: Float,
    val yieldPerSqMUnit: YieldUnit,
    val companionPlants: List<String>,
    val climateZones: ClimateZones,
    val spacing: SpacingRequirement,
    val sunlight: SunlightRequirement,
    val dailySunlight: DailySunlightRequirement,
    val soilTypes: List<SoilType>,
    val waterRequirement: WaterRequirement,
    val growthHabit: GrowthHabit,
    val growingTips: List<GrowingTip>
)

fun Plant.toEntity() = PlantEntity(
    id = id,
    name = name,
    timeToHarvest = timeToHarvest,
    yieldPerPlantFrom = yieldPerPlant.from,
    yieldPerPlantTo = yieldPerPlant.to,
    yieldPerPlantUnit = yieldPerPlant.unit,
    yieldPerSqMFrom = yieldPerSqM.from,
    yieldPerSqMTo = yieldPerSqM.to,
    yieldPerSqMUnit = yieldPerSqM.unit,
    companionPlants = companionPlants,
    climateZones = climateZones,
    spacing = spacing,
    sunlight = sunlight,
    dailySunlight = dailySunlight,
    soilTypes = soilType,
    waterRequirement = waterRequirement,
    growthHabit = growthHabit,
    growingTips = growingTips
)

fun PlantEntity.toDomain() = Plant(
    id = id ?: 0,
    name = name,
    timeToHarvest = timeToHarvest,
    yieldPerPlant = PlantYield(
        from = yieldPerPlantFrom,
        to = yieldPerPlantTo,
        unit = yieldPerPlantUnit
    ),
    yieldPerSqM = PlantYield(
        from = yieldPerSqMFrom,
        to = yieldPerSqMTo,
        unit = yieldPerSqMUnit
    ),
    companionPlants = companionPlants,
    climateZones = climateZones,
    spacing = spacing,
    sunlight = sunlight,
    dailySunlight = dailySunlight,
    soilType = soilTypes,
    waterRequirement = waterRequirement,
    growthHabit = growthHabit,
    growingTips = growingTips
)
