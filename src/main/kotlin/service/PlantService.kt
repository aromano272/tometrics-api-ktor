package com.tometrics.api.service

import com.tometrics.api.db.PlantDao
import com.tometrics.api.db.models.toDomain
import com.tometrics.api.domain.models.NotFoundException
import com.tometrics.api.domain.models.Plant
import com.tometrics.api.domain.models.PlantId

interface PlantService {
    suspend fun getAll(): List<Plant>
    suspend fun getAllByIds(ids: Set<PlantId>): List<Plant>
    suspend fun getById(id: PlantId): Plant
}

class DefaultPlantService(
    private val plantDao: PlantDao
) : PlantService {
    override suspend fun getAll(): List<Plant> = plantDao.getAll().map { it.toDomain() }

    override suspend fun getAllByIds(ids: Set<PlantId>): List<Plant> =
        plantDao.getAllByIds(ids).map { it.toDomain() }

    override suspend fun getById(id: PlantId): Plant =
        plantDao.getById(id)?.toDomain() ?: throw NotFoundException("Plant #$id not found")
}

