package com.tometrics.api.db

import com.tometrics.api.db.db.HarvestDb
import com.tometrics.api.db.models.HarvestEntity
import com.tometrics.api.domain.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

interface HarvestDao {
    suspend fun getAll(userId: UserId): List<HarvestEntity>

    suspend fun getAllByPlantingId(
        userId: UserId,
        plantingId: PlantingId
    ): List<HarvestEntity>

    suspend fun getAllByPlantId(
        userId: UserId,
        plantId: PlantingId
    ): List<HarvestEntity>

    suspend fun findById(id: HarvestId): HarvestEntity?

    suspend fun insert(
        plantingId: PlantingId,
        quantity: Float,
        unit: YieldUnit,
        createdAt: Instant? = null,
    ): HarvestId

    suspend fun delete(id: HarvestId): Int
}

class DefaultHarvestDao(
    private val db: HarvestDb
) : HarvestDao {

    override suspend fun getAll(userId: UserId): List<HarvestEntity> = withContext(Dispatchers.IO) {
        db.getAll(userId)
    }

    override suspend fun getAllByPlantingId(
        userId: UserId,
        plantingId: PlantingId
    ): List<HarvestEntity> = withContext(Dispatchers.IO) {
        db.getAllByPlantingId(userId, plantingId)
    }

    override suspend fun getAllByPlantId(
        userId: UserId,
        plantId: PlantId
    ): List<HarvestEntity> = withContext(Dispatchers.IO) {
        db.getAllByPlantId(userId, plantId)
    }

    override suspend fun findById(id: HarvestId): HarvestEntity? = withContext(Dispatchers.IO) {
        db.findById(id)
    }

    override suspend fun insert(
        plantingId: PlantingId,
        quantity: Float,
        unit: YieldUnit,
        createdAt: Instant?,
    ): HarvestId = withContext(Dispatchers.IO) {
        db.insert(
            plantingId,
            quantity,
            unit,
            createdAt,
        )
    }

    override suspend fun delete(id: HarvestId): Int = withContext(Dispatchers.IO) {
        db.delete(id)
    }

}
