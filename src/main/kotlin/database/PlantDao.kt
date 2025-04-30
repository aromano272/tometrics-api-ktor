package com.sproutscout.api.database

import com.sproutscout.api.model.Plant
import com.sproutscout.api.model.PlantId

interface PlantDao {
    fun getAll(): List<Plant>
    fun getById(id: PlantId): Plant?
    fun insert(plant: Plant): Plant
    fun insertAll(plants: List<Plant>): List<Plant>
} 

class DefaultPlantDao(
    private val db: PlantDb
) : PlantDao {
    override fun getAll(): List<Plant> {
        return db.getAll().map { it.toPlant() }
    }

    override fun getById(id: PlantId): Plant? {
        return db.getById(id)?.toPlant()
    }

    override fun insert(plant: Plant): Plant {
        val entity = PlantEntity.fromPlant(plant)
        db.insert(
            name = entity.name,
            timeToHarvest = entity.timeToHarvest,
            yieldPerPlantFrom = entity.yieldPerPlantFrom,
            yieldPerPlantTo = entity.yieldPerPlantTo,
            yieldPerPlantUnit = entity.yieldPerPlantUnit,
            yieldPerSqMFrom = entity.yieldPerSqMFrom,
            yieldPerSqMTo = entity.yieldPerSqMTo,
            yieldPerSqMUnit = entity.yieldPerSqMUnit,
            companionPlants = entity.companionPlants,
            climateZones = entity.climateZones,
            spacing = entity.spacing,
            sunlight = entity.sunlight,
            dailySunlight = entity.dailySunlight,
            soilTypes = entity.soilTypes,
            waterRequirement = entity.waterRequirement,
            growthHabit = entity.growthHabit,
            growingTips = entity.growingTips
        )
        return plant
    }

    override fun insertAll(plants: List<Plant>): List<Plant> {
        return plants.map { insert(it) }
    }
}
