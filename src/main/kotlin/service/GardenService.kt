package com.tometrics.api.service

import com.tometrics.api.db.GardenDao
import com.tometrics.api.db.models.toDomain
import com.tometrics.api.domain.models.*
import java.time.Instant
import java.time.temporal.ChronoUnit

interface GardenService {
    suspend fun getAll(requester: Requester): List<Planting>
    suspend fun getById(requester: Requester, id: PlantingId): Planting
    suspend fun delete(requester: Requester, id: PlantingId)
    suspend fun update(requester: Requester, id: PlantingId, newQuantity: Int): Planting
    suspend fun add(requester: Requester, plantId: PlantId, quantity: Int): Planting

    suspend fun getAllReadyForHarvestToday(): Map<UserId, List<Planting>>
}

class DefaultGardenService(
    private val gardenDao: GardenDao,
    private val plantService: PlantService,
) : GardenService {

    override suspend fun getAll(requester: Requester): List<Planting> {
        return gardenDao.getAll(requester.userId).map { planting ->
            val plant = plantService.getById(planting.plantId)
            planting.toDomain(plant)
        }
    }

    override suspend fun getById(requester: Requester, id: PlantingId): Planting {
        val planting = gardenDao.find(id) ?: throw NotFoundException("Planting not found")
        if (planting.userId != requester.userId) throw BadRequestException("Planting doesn't belong to this user")
        val plant = plantService.getById(planting.plantId)

        return planting.toDomain(plant)
    }

    override suspend fun delete(requester: Requester, id: PlantingId) {
        val planting = gardenDao.find(id) ?: throw NotFoundException("Planting not found")
        if (planting.userId != requester.userId) throw BadRequestException("Planting doesn't belong to this user")
        gardenDao.delete(id)
    }

    override suspend fun update(requester: Requester, id: PlantingId, newQuantity: Int): Planting {
        val planting = gardenDao.find(id) ?: throw NotFoundException("Planting not found")
        if (planting.userId != requester.userId) throw BadRequestException("Planting doesn't belong to this user")
        gardenDao.update(id, newQuantity)

        return getById(requester, id)
    }

    override suspend fun add(requester: Requester, plantId: PlantId, quantity: Int): Planting {
        val plant = plantService.getById(plantId)
        val id: PlantingId = gardenDao.insert(
            userId = requester.userId,
            plantId = plantId,
            quantity = quantity,
            readyToHarvestAt = Instant.now().plus(plant.timeToHarvest.toLong(), ChronoUnit.DAYS),
        )
        return getById(requester, id)
    }

    override suspend fun getAllReadyForHarvestToday(): Map<UserId, List<Planting>> {
        val plantings = gardenDao.getAllReadyForHarvestToday()
        val plantIds = plantings.map { it.plantId }.toSet()
        val plantsMap = plantService.getAllByIds(plantIds).associateBy { it.id }
        return plantings
            .groupBy { it.userId }
            .mapValues { (_, plantings) ->
                plantings.mapNotNull { planting ->
                    val plant = plantsMap[planting.plantId] ?: return@mapNotNull null
                    planting.toDomain(plant)
                }
            }
    }
}