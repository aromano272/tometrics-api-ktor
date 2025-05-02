package com.tometrics.api.db

import com.tometrics.api.db.models.PlantingEntity
import com.tometrics.api.domain.models.PlantId
import com.tometrics.api.domain.models.PlantingId
import com.tometrics.api.domain.models.UserId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

interface GardenDao {
    suspend fun getAll(userId: UserId): List<PlantingEntity>
    suspend fun find(id: PlantingId): PlantingEntity?
    suspend fun delete(id: PlantingId): Int
    suspend fun update(id: PlantingId, newQuantity: Int): Int
    suspend fun insert(
        userId: UserId,
        plantId: PlantId,
        quantity: Int,
        readyToHarvestAt: Instant,
    ): PlantingId
    suspend fun getAllReadyForHarvestToday(): List<PlantingEntity>
}

class DefaultGardenDao(
    private val db: GardenDb
) : GardenDao {

    override suspend fun getAll(userId: UserId): List<PlantingEntity> = withContext(Dispatchers.IO) {
       db.getAll(userId)
    }

    override suspend fun find(id: PlantingId): PlantingEntity? = withContext(Dispatchers.IO) {
       db.find(id)
    }

    override suspend fun delete(id: PlantingId): Int = withContext(Dispatchers.IO) {
       db.delete(id)
    }

    override suspend fun update(id: PlantingId, newQuantity: Int): Int = withContext(Dispatchers.IO) {
       db.update(id, newQuantity)
    }

    override suspend fun insert(
        userId: UserId,
        plantId: PlantId,
        quantity: Int,
        readyToHarvestAt: Instant,
    ): PlantingId = withContext(Dispatchers.IO) {
       db.insert(userId, plantId, quantity, readyToHarvestAt)
    }

    override suspend fun getAllReadyForHarvestToday(): List<PlantingEntity> = withContext(Dispatchers.IO) {
        db.getAllReadyForHarvestToday()
    }

}