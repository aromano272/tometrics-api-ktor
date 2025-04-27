package com.sproutscout.api.routes.models

import com.sproutscout.api.model.Plant
import kotlinx.serialization.Serializable

@Serializable
data class GetAllPlantsResponse(
    val plants: List<Plant>,
)