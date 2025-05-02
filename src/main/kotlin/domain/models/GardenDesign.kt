package com.tometrics.api.domain.models

import kotlinx.serialization.Serializable

typealias ContiguousCells = List<GardenCell>
typealias ContiguousCellRefs = List<GardenCellRef>

@Serializable
data class GardenDesign(
    val cells: List<List<GardenCell>>,
    val plantedContiguous: List<ContiguousCells>,
    val summary: List<PlantingYield>,
)

@Serializable
data class PlantingYield(
    val plant: Plant,
    val quantity: Int,
    val yield: PlantYield,
) {
    operator fun plus(other: PlantingYield): PlantingYield {
        if (plant.id != other.plant.id) throw IllegalArgumentException("Cannot add 2 yields with different plants")
        return PlantingYield(
            plant = plant,
            quantity = quantity + other.quantity,
            yield = yield + other.yield,
        )
    }
}
