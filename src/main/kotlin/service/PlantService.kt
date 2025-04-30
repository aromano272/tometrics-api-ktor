package com.sproutscout.api.service

import com.sproutscout.api.database.PlantDao
import com.sproutscout.api.domain.models.NotFoundException
import com.sproutscout.api.model.Plant
import com.sproutscout.api.model.PlantId

interface PlantService {
    suspend fun getAll(): List<Plant>
    suspend fun getById(id: PlantId): Plant
}

class DefaultPlantService(
    private val plantDao: PlantDao
) : PlantService {
    override suspend fun getAll(): List<Plant> {
        return plantDao.getAll()
    }

    override suspend fun getById(id: PlantId): Plant {
        return plantDao.getById(id)
            ?: throw NotFoundException("Plant #$id not found")
    }
}

