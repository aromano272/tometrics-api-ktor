package com.sproutscout.api.database

import com.sproutscout.api.model.*
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper

@RegisterKotlinMapper(PlantEntity::class)
interface PlantDb {
    @SqlQuery("""
        SELECT id, name, time_to_harvest,
               yield_per_plant_min, yield_per_plant_max, yield_per_plant_unit,
               yield_per_sqm_min, yield_per_sqm_max, yield_per_sqm_unit,
               companion_plants, climate_zones, spacing, sunlight,
               daily_sunlight, soil_types, water_requirement,
               growth_habit, growing_tips
        FROM plants
    """)
    fun getAll(): List<PlantEntity>

    @SqlQuery("""
        SELECT id, name, time_to_harvest,
               yield_per_plant_min, yield_per_plant_max, yield_per_plant_unit,
               yield_per_sqm_min, yield_per_sqm_max, yield_per_sqm_unit,
               companion_plants, climate_zones, spacing, sunlight,
               daily_sunlight, soil_types, water_requirement,
               growth_habit, growing_tips
        FROM plants
        WHERE id = :id
    """)
    fun getById(@Bind("id") id: Int): PlantEntity?

    @SqlUpdate("""
        INSERT INTO plants (
            name, time_to_harvest,
            yield_per_plant_min, yield_per_plant_max, yield_per_plant_unit,
            yield_per_sqm_min, yield_per_sqm_max, yield_per_sqm_unit,
            companion_plants, climate_zones, spacing, sunlight,
            daily_sunlight, soil_types, water_requirement,
            growth_habit, growing_tips
        ) VALUES (
            :name, :timeToHarvest,
            :yieldPerPlantFrom, :yieldPerPlantTo, :yieldPerPlantUnit,
            :yieldPerSqMFrom, :yieldPerSqMTo, :yieldPerSqMUnit,
            :companionPlants, :climateZones, :spacing, :sunlight,
            :dailySunlight, :soilTypes, :waterRequirement,
            :growthHabit, :growingTips
        )
    """)
    fun insert(
        @Bind("name") name: String,
        @Bind("timeToHarvest") timeToHarvest: Int,
        @Bind("yieldPerPlantFrom") yieldPerPlantFrom: Float,
        @Bind("yieldPerPlantTo") yieldPerPlantTo: Float,
        @Bind("yieldPerPlantUnit") yieldPerPlantUnit: String,
        @Bind("yieldPerSqMFrom") yieldPerSqMFrom: Float,
        @Bind("yieldPerSqMTo") yieldPerSqMTo: Float,
        @Bind("yieldPerSqMUnit") yieldPerSqMUnit: String,
        @Bind("companionPlants") companionPlants: List<String>,
        @Bind("climateZones") climateZones: ClimateZones,
        @Bind("spacing") spacing: String,
        @Bind("sunlight") sunlight: String,
        @Bind("dailySunlight") dailySunlight: String,
        @Bind("soilTypes") soilTypes: List<SoilType>,
        @Bind("waterRequirement") waterRequirement: String,
        @Bind("growthHabit") growthHabit: String,
        @Bind("growingTips") growingTips: List<GrowingTip>
    )
} 