package com.sproutscout.api.service

import com.sproutscout.api.model.Plant
import com.sproutscout.api.model.PlantId
import com.sproutscout.api.model.plants

interface PlantService {
    suspend fun getAll(): List<Plant>
    suspend fun getById(id: PlantId): Plant?
}

class DefaultPlantService(

) : PlantService {
    override suspend fun getAll(): List<Plant> {
        return plants
    }

    override suspend fun getById(id: PlantId): Plant? {
        return plants.find { it.id == id }
    }


}