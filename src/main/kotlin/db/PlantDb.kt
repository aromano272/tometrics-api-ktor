package com.tometrics.api.db

import com.tometrics.api.db.models.PlantEntity
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

@RegisterKotlinMapper(PlantEntity::class)
interface PlantDb {
    @SqlQuery("""
        SELECT id, name, time_to_harvest,
               yield_per_plant_from, yield_per_plant_to, yield_per_plant_unit,
               yield_per_sqm_from, yield_per_sqm_to, yield_per_sqm_unit,
               companion_plants, climate_zones, spacing, sunlight,
               daily_sunlight, soil_types, water_requirement,
               growth_habit, growing_tips
        FROM plants
    """)
    fun getAll(): List<PlantEntity>

    @SqlQuery("""
        SELECT id, name, time_to_harvest,
               yield_per_plant_from, yield_per_plant_to, yield_per_plant_unit,
               yield_per_sqm_from, yield_per_sqm_to, yield_per_sqm_unit,
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
            yield_per_plant_from, yield_per_plant_to, yield_per_plant_unit,
            yield_per_sqm_from, yield_per_sqm_to, yield_per_sqm_unit,
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
    fun insert(@BindBean entity: PlantEntity)
} 