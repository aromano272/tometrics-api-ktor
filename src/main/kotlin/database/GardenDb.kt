package com.sproutscout.api.database

import com.sproutscout.api.database.models.PlantingEntity
import com.sproutscout.api.domain.models.PlantingId
import com.sproutscout.api.domain.models.UserId
import com.sproutscout.api.model.PlantId
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

@RegisterKotlinMapper(PlantingEntity::class)
interface GardenDb {

    @SqlQuery("SELECT * FROM plantings WHERE user_id = :userId")
    fun getAll(@Bind("userId") userId: UserId): List<PlantingEntity>

    @SqlQuery("SELECT * FROM plantings WHERE id = :id")
    fun find(@Bind("id") id: PlantingId): PlantingEntity?

    @SqlUpdate("DELETE FROM plantings WHERE id = :id")
    fun delete(@Bind("id") id: PlantingId): Int

    @SqlUpdate("UPDATE plantings SET quantity = :newQuantity WHERE id = :id")
    fun update(@Bind("id") id: PlantingId, @Bind("newQuantity") newQuantity: Int): Int

    @SqlUpdate(
        """
        INSERT INTO plantings (user_id, plant_id, quantity) 
        VALUES (:userId, :plantId, :quantity)
        """
    )
    @GetGeneratedKeys
    fun insert(
        @Bind("userId") userId: UserId,
        @Bind("plantId") plantId: PlantId,
        @Bind("quantity") quantity: Int
    ): PlantingId

    @SqlQuery("SELECT * FROM plantings")
    fun getAll(): List<PlantingEntity>

}
