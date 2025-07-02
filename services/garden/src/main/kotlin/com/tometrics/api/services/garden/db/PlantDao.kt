package com.tometrics.api.services.garden.db

import com.tometrics.api.services.garden.db.models.PlantEntity
import com.tometrics.api.services.garden.domain.models.PlantId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface PlantDao {
    suspend fun getAll(): List<PlantEntity>
    suspend fun getAllByIds(ids: Set<PlantId>): List<PlantEntity>
    suspend fun getById(id: PlantId): PlantEntity?
}

class DefaultPlantDao(
    private val db: PlantDb
) : PlantDao {

    override suspend fun getAll(): List<PlantEntity> = withContext(Dispatchers.IO) {
        db.getAll()
    }

    override suspend fun getAllByIds(ids: Set<PlantId>): List<PlantEntity> = withContext(Dispatchers.IO) {
        db.getAllByIds(ids)
    }

    override suspend fun getById(id: PlantId): PlantEntity? = withContext(Dispatchers.IO) {
        db.getById(id)
    }

}
