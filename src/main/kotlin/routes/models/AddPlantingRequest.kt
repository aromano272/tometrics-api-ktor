package com.sproutscout.api.routes.models

import com.sproutscout.api.model.PlantId
import kotlinx.serialization.Serializable

@Serializable
data class AddPlantingRequest(
    val plantId: PlantId,
    val quantity: Int,
)