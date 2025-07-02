package com.tometrics.api.services.garden.routes.models

import com.tometrics.api.services.garden.domain.models.Plant
import kotlinx.serialization.Serializable

@Serializable
data class GetAllPlantsResponse(
    val plants: List<Plant>,
)