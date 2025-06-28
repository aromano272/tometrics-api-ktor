package com.tometrics.api.services.garden.db

import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.garden.db.models.PlantingEntity
import com.tometrics.api.services.garden.domain.models.PlantId
import com.tometrics.api.services.garden.domain.models.PlantingId

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

interface GardenDao {
    suspend fun getAll(userId: UserId): List<PlantingEntity>
    suspend fun find(id: PlantingId): PlantingEntity?
    suspend fun delete(id: PlantingId): Int
    suspend fun update(
        id: PlantingId,
        newQuantity: Int?,
        newName: String?,
        newDiary: String?,
        newHarvested: Boolean?,
    ): Int
    suspend fun insert(
        userId: UserId,
        plantId: PlantId,
        name: String?,
        quantity: Int,
        readyToHarvestAt: Instant,
        diary: String = "",
        harvested: Boolean = false,
    ): PlantingId

    suspend fun getAllReadyForHarvestToday(): List<PlantingEntity>
    suspend fun getSamePlantPlantings(userId: UserId, plantId: PlantId): List<PlantingEntity>
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

    override suspend fun update(id: PlantingId, newQuantity: Int?, newName: String?, newDiary: String?, newHarvested: Boolean?): Int = withContext(Dispatchers.IO) {
        db.update(id, newQuantity, newName, newDiary, newHarvested)
    }

    override suspend fun insert(
        userId: UserId,
        plantId: PlantId,
        name: String?,
        quantity: Int,
        readyToHarvestAt: Instant,
        diary: String,
        harvested: Boolean,
    ): PlantingId = withContext(Dispatchers.IO) {
        db.insert(userId, plantId, name, quantity, readyToHarvestAt, diary, harvested)
    }

    override suspend fun getAllReadyForHarvestToday(): List<PlantingEntity> = withContext(Dispatchers.IO) {
        db.getAllReadyForHarvestToday()
    }

    override suspend fun getSamePlantPlantings(
        userId: UserId,
        plantId: PlantId,
    ): List<PlantingEntity> = withContext(Dispatchers.IO) {
        db.getSamePlantPlantings(userId, plantId)
    }

}
