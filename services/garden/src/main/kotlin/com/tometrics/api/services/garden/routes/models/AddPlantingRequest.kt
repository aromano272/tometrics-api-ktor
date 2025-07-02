package com.tometrics.api.services.garden.routes.models

import com.tometrics.api.services.garden.domain.models.PlantId
import kotlinx.serialization.Serializable

@Serializable
data class AddPlantingRequest(
    val plantId: PlantId,
    val quantity: Int,
)