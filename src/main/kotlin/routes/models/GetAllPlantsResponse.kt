package com.tometrics.api.routes.models

import com.tometrics.api.domain.models.Plant
import kotlinx.serialization.Serializable

@Serializable
data class GetAllPlantsResponse(
    val plants: List<Plant>,
)