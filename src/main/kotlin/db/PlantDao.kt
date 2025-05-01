package com.sproutscout.api.db

import com.sproutscout.api.db.models.toDomain
import com.sproutscout.api.model.Plant
import com.sproutscout.api.model.PlantId

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
