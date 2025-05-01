package com.tometrics.api.db

import com.tometrics.api.db.models.toDomain
import com.tometrics.api.model.Plant
import com.tometrics.api.model.PlantId

interface PlantDao {
    fun getAll(): List<Plant>
    fun getById(id: PlantId): Plant?
}

class DefaultPlantDao(
    private val db: PlantDb
) : PlantDao {

    override fun getAll(): List<Plant> =
        db.getAll().map { it.toDomain() }

    override fun getById(id: PlantId): Plant? =
        db.getById(id)?.toDomain()

}
