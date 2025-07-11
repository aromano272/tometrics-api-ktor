package com.tometrics.api.services.garden.db

import com.tometrics.api.services.garden.db.models.PlantEntity
import com.tometrics.api.services.garden.domain.models.PlantId
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindList
import org.jdbi.v3.sqlobject.kotlin.BindKotlin
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

@RegisterKotlinMapper(PlantEntity::class)
interface PlantDb {
    @SqlQuery("SELECT * FROM plants ORDER BY name")
    fun getAll(): List<PlantEntity>

    @SqlQuery("SELECT * FROM plants WHERE id = ANY(:ids)")
    fun getAllByIds(@Bind("ids") ids: Set<PlantId>): List<PlantEntity>

    @SqlQuery(
        """
        SELECT *
        FROM plants
        WHERE id = :id
    """
    )
    fun getById(@Bind("id") id: Int): PlantEntity?

    @SqlUpdate(
        """
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
    """
    )
    fun insert(@BindKotlin entity: PlantEntity)
} 