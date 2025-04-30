package com.sproutscout.api.service

import com.sproutscout.api.db.GardenDao
import com.sproutscout.api.db.models.toDomain
import com.sproutscout.api.domain.models.*
import com.sproutscout.api.model.PlantId

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
        val id: PlantingId = gardenDao.insert(requester.userId, plantId, quantity)
        return getById(requester, id)
    }

    override suspend fun getAllReadyForHarvestToday(): Map<UserId, List<Planting>> {
        throw NotImplementedError()
//        val plantings = gardenDao.getAll()
//            .filter { planting ->
//                LocalDate.from(planting.createdAt).plusDays(planting.plant) == LocalDate.now()
//                timeToHarvest
//            }
//            .groupBy { it.userId }
//            .mapValues { (userId, plantings) ->
//                plantings.map { planting ->
//                    val plant = plantService.getById(planting.plantId)
//                    planting.toDomain(plant)
//                }
//            }
//        return plantings
    }

}