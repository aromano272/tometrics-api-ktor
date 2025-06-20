package com.tometrics.api.db

import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.db.models.HarvestEntity
import com.tometrics.api.domain.models.*
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.kotlin.RegisterKotlinMapper
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.time.Instant

@RegisterKotlinMapper(HarvestEntity::class)
interface HarvestDb {

    @SqlQuery(
        """
        SELECT harvests.* FROM harvests
        JOIN plantings ON harvests.planting_id = plantings.id
        WHERE plantings.user_id = :userId
    """
    )
    fun getAll(@Bind("userId") userId: UserId): List<HarvestEntity>

    @SqlQuery(
        """
        SELECT harvests.* FROM harvests
        JOIN plantings ON harvests.planting_id = plantings.id
        WHERE plantings.user_id = :userId
        AND plantings.id = :plantingId
    """
    )
    fun getAllByPlantingId(
        @Bind("userId") userId: UserId,
        @Bind("plantingId") plantingId: PlantingId,
    ): List<HarvestEntity>

    @SqlQuery(
        """
        SELECT harvests.* FROM harvests
        JOIN plantings ON harvests.planting_id = plantings.id
        WHERE plantings.user_id = :userId
        AND plantings.plant_id = :plantId
    """
    )
    fun getAllByPlantId(
        @Bind("userId") userId: UserId,
        @Bind("plantId") plantId: PlantId,
    ): List<HarvestEntity>

    @SqlQuery(
        """
        SELECT * FROM harvests
        WHERE id = :id
    """
    )
    fun findById(@Bind("id") id: HarvestId): HarvestEntity?

    @SqlUpdate(
        """
            INSERT INTO harvests (planting_id, quantity, unit, created_at)
            VALUES (:plantingId, :quantity, :unit, COALESCE(:createdAt, NOW()))
            
        """
    )
    @GetGeneratedKeys
    fun insert(
        @Bind("plantingId") plantingId: PlantingId,
        @Bind("quantity") quantity: Float,
        @Bind("unit") unit: YieldUnit,
        @Bind("createdAt") createdAt: Instant?,
    ): HarvestId

    @SqlUpdate("DELETE FROM harvests WHERE id = :id")
    fun delete(@Bind("id") id: HarvestId): Int

}