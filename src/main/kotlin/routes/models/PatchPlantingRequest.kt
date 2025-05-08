package com.tometrics.api.routes.models

import kotlinx.serialization.Serializable

@Serializable
data class PatchPlantingRequest(
    val newQuantity: Int? = null,
    val newName: String? = null,
    val newDiary: String? = null,
    val newHarvested: Boolean? = null,
)
