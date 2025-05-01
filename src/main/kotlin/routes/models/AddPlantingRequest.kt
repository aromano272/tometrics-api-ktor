package com.tometrics.api.routes.models

import com.tometrics.api.model.PlantId
import kotlinx.serialization.Serializable

@Serializable
data class AddPlantingRequest(
    val plantId: PlantId,
    val quantity: Int,
)