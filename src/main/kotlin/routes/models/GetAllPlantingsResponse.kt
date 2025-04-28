package com.sproutscout.api.routes.models

import com.sproutscout.api.domain.models.Planting
import kotlinx.serialization.Serializable

@Serializable
data class GetAllPlantingsResponse(
    val plantings: List<Planting>,
)