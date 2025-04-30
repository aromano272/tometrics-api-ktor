package com.sproutscout.api.database

import com.sproutscout.api.database.models.PlantingEntity
import com.sproutscout.api.domain.models.PlantingId
import com.sproutscout.api.domain.models.UserId
import com.sproutscout.api.model.PlantId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface GardenDao {
    suspend fun getAll(userId: UserId): List<PlantingEntity>
    suspend fun find(id: PlantingId): PlantingEntity?
    suspend fun delete(id: PlantingId): Int
    suspend fun update(id: PlantingId, newQuantity: Int): Int
    suspend fun insert(userId: UserId, plantId: PlantId, quantity: Int): PlantingId
    // TODO move the plants into a table so we can make the query do the filtering rather than the service, getAllReadyForHarvestToday
    suspend fun getAll(): List<PlantingEntity>
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
        quantity: Int
    ): PlantingId = withContext(Dispatchers.IO) {
       db.insert(userId, plantId, quantity)
    }

    override suspend fun getAll(): List<PlantingEntity> = withContext(Dispatchers.IO) {
        db.getAll()
    }

}