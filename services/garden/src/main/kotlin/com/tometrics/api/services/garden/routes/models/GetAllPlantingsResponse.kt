package com.tometrics.api.services.garden.routes.models

import com.tometrics.api.services.garden.domain.models.Planting
import kotlinx.serialization.Serializable

@Serializable
data class GetAllPlantingsResponse(
    val plantings: List<Planting>,
)