package com.tometrics.api.service

import com.tometrics.api.db.PlantDao
import com.tometrics.api.domain.models.NotFoundException
import com.tometrics.api.domain.models.Plant
import com.tometrics.api.domain.models.PlantId

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

