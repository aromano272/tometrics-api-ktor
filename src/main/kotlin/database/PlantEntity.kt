package com.sproutscout.api.database

import com.sproutscout.api.model.*

data class PlantEntity(
    val id: Int? = null,
    val name: String,
    val timeToHarvest: Int,
    val yieldPerPlantFrom: Float,
    val yieldPerPlantTo: Float,
    val yieldPerPlantUnit: String,
    val yieldPerSqMFrom: Float,
    val yieldPerSqMTo: Float,
    val yieldPerSqMUnit: String,
    val companionPlants: List<String>,
    val climateZones: ClimateZones,
    val spacing: String,
    val sunlight: String,
    val dailySunlight: String,
    val soilTypes: List<SoilType>,
    val waterRequirement: String,
    val growthHabit: String,
    val growingTips: List<GrowingTip>
) {
    fun toPlant(): Plant {
        return Plant(
            id = id ?: 0,
            name = name,
            timeToHarvest = timeToHarvest,
            yieldPerPlant = PlantYield(
                from = yieldPerPlantFrom,
                to = yieldPerPlantTo,
                unit = YieldUnit.valueOf(yieldPerPlantUnit)
            ),
            yieldPerSqM = PlantYield(
                from = yieldPerSqMFrom,
                to = yieldPerSqMTo,
                unit = YieldUnit.valueOf(yieldPerSqMUnit)
            ),
            companionPlants = companionPlants,
            climateZones = climateZones,
            spacing = SpacingRequirement.valueOf(spacing),
            sunlight = SunlightRequirement.valueOf(sunlight),
            dailySunlight = DailySunlightRequirement.valueOf(dailySunlight),
            soilType = soilTypes,
            waterRequirement = WaterRequirement.valueOf(waterRequirement),
            growthHabit = PlantGrowthHabit.valueOf(growthHabit),
            growingTips = growingTips
        )
    }

    companion object {
        fun fromPlant(plant: Plant): PlantEntity {
            return PlantEntity(
                id = plant.id,
                name = plant.name,
                timeToHarvest = plant.timeToHarvest,
                yieldPerPlantFrom = plant.yieldPerPlant.from,
                yieldPerPlantTo = plant.yieldPerPlant.to,
                yieldPerPlantUnit = plant.yieldPerPlant.unit.name,
                yieldPerSqMFrom = plant.yieldPerSqM.from,
                yieldPerSqMTo = plant.yieldPerSqM.to,
                yieldPerSqMUnit = plant.yieldPerSqM.unit.name,
                companionPlants = plant.companionPlants,
                climateZones = plant.climateZones,
                spacing = plant.spacing.name,
                sunlight = plant.sunlight.name,
                dailySunlight = plant.dailySunlight.name,
                soilTypes = plant.soilType,
                waterRequirement = plant.waterRequirement.name,
                growthHabit = plant.growthHabit.name,
                growingTips = plant.growingTips
            )
        }
    }
} 