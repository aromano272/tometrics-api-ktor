package com.tometrics.api.routes.models

import com.tometrics.api.domain.models.PlantId
import kotlinx.serialization.Serializable

@Serializable
data class AddPlantingRequest(
    val plantId: PlantId,
    val quantity: Int,
)