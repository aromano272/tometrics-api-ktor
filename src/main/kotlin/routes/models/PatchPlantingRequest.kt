package com.sproutscout.api.routes.models

import kotlinx.serialization.Serializable

@Serializable
data class PatchPlantingRequest(
    val newQuantity: Int,
)