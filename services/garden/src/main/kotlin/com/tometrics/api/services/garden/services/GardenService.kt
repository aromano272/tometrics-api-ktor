package com.tometrics.api.services.garden.services

import com.tometrics.api.auth.domain.models.Requester
import com.tometrics.api.common.domain.models.BadRequestError
import com.tometrics.api.common.domain.models.NotFoundError
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.garden.db.GardenDao
import com.tometrics.api.services.garden.db.models.toDomain
import com.tometrics.api.services.garden.domain.models.PlantId
import com.tometrics.api.services.garden.domain.models.Planting
import com.tometrics.api.services.garden.domain.models.PlantingId
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

interface GardenService {
    suspend fun getAllReadyForHarvestToday(): Map<UserId, List<Planting>>
    suspend fun getAll(requester: Requester): List<Planting>
    suspend fun getById(requester: Requester, id: PlantingId): Planting
    suspend fun delete(requester: Requester, id: PlantingId)
    suspend fun update(
        requester: Requester,
        id: PlantingId,
        newQuantity: Int?,
        newName: String? = null,
        newDiary: String? = null,
        newHarvested: Boolean? = null,
    ): Planting
    suspend fun add(requester: Requester, plantId: PlantId, quantity: Int): Planting
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
        val planting = gardenDao.find(id) ?: throw NotFoundError("Planting not found")
        if (planting.userId != requester.userId) throw BadRequestError("Planting doesn't belong to this user")
        val plant = plantService.getById(planting.plantId)

        return planting.toDomain(plant)
    }

    override suspend fun delete(requester: Requester, id: PlantingId) {
        val planting = gardenDao.find(id) ?: throw NotFoundError("Planting not found")
        if (planting.userId != requester.userId) throw BadRequestError("Planting doesn't belong to this user")
        gardenDao.delete(id)
    }

    override suspend fun update(requester: Requester, id: PlantingId, newQuantity: Int?, newName: String?, newDiary: String?, newHarvested: Boolean?): Planting {
        val planting = gardenDao.find(id) ?: throw NotFoundError("Planting not found")
        if (planting.userId != requester.userId) throw BadRequestError("Planting doesn't belong to this user")
        gardenDao.update(id, newQuantity, newName, newDiary, newHarvested)

        return getById(requester, id)
    }

    override suspend fun add(requester: Requester, plantId: PlantId, quantity: Int): Planting {
        val plant = plantService.getById(plantId)
        val samePlantPlantings = gardenDao.getSamePlantPlantings(
            requester.userId,
            plantId,
        )
        val plantingName = if (samePlantPlantings.isEmpty()) {
            plant.name
        } else {
            val dateStr = DateTimeFormatter
                .ofPattern("yyyy-MM-dd")
                .withZone(ZoneId.systemDefault())
                .format(Instant.now())
            val count = samePlantPlantings.count {
                it.createdAt.truncatedTo(ChronoUnit.DAYS) == Instant.now().truncatedTo(ChronoUnit.DAYS)
            }

            if (count == 0) {
                "${plant.name} $dateStr"
            } else {
                "${plant.name} $dateStr #${count + 1}"
            }
        }
        val id: PlantingId = gardenDao.insert(
            userId = requester.userId,
            plantId = plantId,
            name = plantingName,
            quantity = quantity,
            readyToHarvestAt = Instant.now().plus(plant.timeToHarvest.toLong(), ChronoUnit.DAYS),
            diary = "",
            harvested = false,
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

