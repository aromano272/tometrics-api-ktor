package com.tometrics.api.service

import com.tometrics.api.db.HarvestDao
import com.tometrics.api.db.models.toDomain
import com.tometrics.api.domain.models.*
import java.time.Instant

interface HarvestService {
    suspend fun getAll(
        requester: Requester,
        plantingId: PlantingId?,
        plantId: PlantId?,
    ): List<Harvest>

    suspend fun add(
        requester: Requester,
        plantingId: PlantingId,
        quantity: Float,
        unit: YieldUnit,
        createdAt: Millis? = null,
    ): Harvest

    suspend fun delete(
        requester: Requester,
        id: HarvestId,
    )
}

class DefaultHarvestService(
    private val harvestDao: HarvestDao,
    private val gardenService: GardenService,
) : HarvestService {
    override suspend fun getAll(
        requester: Requester,
        plantingId: PlantingId?,
        plantId: PlantId?,
    ): List<Harvest> = if (plantingId != null) {
        harvestDao.getAllByPlantingId(
            requester.userId,
            plantingId,
        ).map { it.toDomain() }
    } else if (plantId != null) {
        harvestDao.getAllByPlantId(
            requester.userId,
            plantId,
        ).map { it.toDomain() }
    } else {
        harvestDao.getAll(requester.userId)
            .map { it.toDomain() }
    }

    override suspend fun add(
        requester: Requester,
        plantingId: PlantingId,
        quantity: Float,
        unit: YieldUnit,
        createdAt: Millis?
    ): Harvest {
        val planting = gardenService.getById(requester, plantingId)
        val id = harvestDao.insert(
            plantingId = planting.id,
            quantity = quantity,
            unit = unit,
            createdAt = createdAt?.let { Instant.ofEpochMilli(it) },
        )
        val harvest = harvestDao.findById(id)
            ?: throw ConflictException("Couldn't create harvest")

        return harvest.toDomain()
    }

    override suspend fun delete(
        requester: Requester,
        id: HarvestId,
    ) {
        val harvest = harvestDao.findById(id) ?: throw NotFoundException("Harvest not found")
        val planting = gardenService.getById(requester, harvest.plantingId)

        harvestDao.delete(id)
    }
}