package com.tometrics.api.services.garden.db.models

import com.tometrics.api.services.garden.domain.models.*
import org.jdbi.v3.json.Json

data class PlantEntity(
    val id: PlantId? = null,
    val name: String,
    val timeToHarvest: Int,
    val yieldPerPlantFrom: Float,
    val yieldPerPlantTo: Float,
    val yieldPerPlantUnit: YieldUnit,
    val yieldPerSqMFrom: Float,
    val yieldPerSqMTo: Float,
    val yieldPerSqMUnit: YieldUnit,
    val companionPlants: List<String>,
    @Json
    val climateZones: ClimateZonesEntity,
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
    climateZones = climateZones.toEntity(),
    spacing = spacing,
    sunlight = sunlight,
    dailySunlight = dailySunlight,
    soilTypes = soilType,
    waterRequirement = waterRequirement,
    growthHabit = growthHabit,
    growingTips = growingTips
)

fun ClimateZones.toEntity() = ClimateZonesEntity(
    temperate = temperate,
    mediterranean = mediterranean,
    continental = continental,
    tropical = tropical,
    arid = arid,
)

data class ClimateZonesEntity(
    val temperate: List<Month>,
    val mediterranean: List<Month>,
    val continental: List<Month>,
    val tropical: List<Month>,
    val arid: List<Month>
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
    climateZones = climateZones.toDomain(),
    spacing = spacing,
    sunlight = sunlight,
    dailySunlight = dailySunlight,
    soilType = soilTypes,
    waterRequirement = waterRequirement,
    growthHabit = growthHabit,
    growingTips = growingTips
)

fun ClimateZonesEntity.toDomain() = ClimateZones(
    temperate = temperate,
    mediterranean = mediterranean,
    continental = continental,
    tropical = tropical,
    arid = arid,
)

