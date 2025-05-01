package com.tometrics.api.routes.models

import kotlinx.serialization.Serializable

@Serializable
data class PatchPlantingRequest(
    val newQuantity: Int,
)