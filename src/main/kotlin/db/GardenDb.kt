package com.tometrics.api.db

import com.tometrics.api.db.models.PlantingEntity
import com.tometrics.api.domain.models.PlantId
import com.tometrics.api.domain.models.PlantingId
import com.tometrics.api.domain.models.UserId
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.time.Instant

@RegisterKotlinMapper(PlantingEntity::class)
interface GardenDb {

    @SqlQuery(
        """
        SELECT * FROM plantings
        WHERE user_id = :userId
    """
    )
    fun getAll(@Bind("userId") userId: UserId): List<PlantingEntity>

    @SqlQuery(
        """
        SELECT * FROM plantings
        WHERE id = :id
        ORDER BY created_at
    """
    )
    fun find(@Bind("id") id: PlantingId): PlantingEntity?

    @SqlUpdate("DELETE FROM plantings WHERE id = :id")
    fun delete(@Bind("id") id: PlantingId): Int

    @SqlUpdate(
        """
            UPDATE plantings 
            SET quantity = COALESCE(:newQuantity, quantity), 
            name = COALESCE(:newName, name),
            diary = COALESCE(:newDiary, diary),
            harvested = COALESCE(:newHarvested, harvested)
            WHERE id = :id
    """
    )
    fun update(
        @Bind("id") id: PlantingId,
        @Bind("newQuantity") newQuantity: Int?,
        @Bind("newName") newName: String?,
        @Bind("newDiary") newDiary: String?,
        @Bind("newHarvested") newHarvested: Boolean?,
    ): Int

    @SqlUpdate(
        """
        INSERT INTO plantings (user_id, plant_id, name, quantity, ready_to_harvest_at, diary, harvested) 
        VALUES (:userId, :plantId, :name, :quantity, :readyToHarvestAt, :diary, :harvested)
        """
    )
    @GetGeneratedKeys
    fun insert(
        @Bind("userId") userId: UserId,
        @Bind("plantId") plantId: PlantId,
        @Bind("name") name: String?,
        @Bind("quantity") quantity: Int,
        @Bind("readyToHarvestAt") readyToHarvestAt: Instant,
        @Bind("diary") diary: String = "",
        @Bind("harvested") harvested: Boolean = false,
    ): PlantingId

    @SqlQuery(
        """
        SELECT * FROM plantings
        WHERE DATE_TRUNC('day', ready_to_harvest_at) = DATE_TRUNC('day', CURRENT_TIMESTAMP)
        ORDER BY created_at
        """
    )
    fun getAllReadyForHarvestToday(): List<PlantingEntity>

}
