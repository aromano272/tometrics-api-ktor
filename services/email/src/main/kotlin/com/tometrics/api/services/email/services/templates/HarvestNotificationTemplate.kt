package com.tometrics.api.services.email.services.templates

import com.tometrics.api.domain.models.Planting
import com.tometrics.api.domain.models.YieldUnit
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.collections.map

data class HarvestNotificationTemplate(
    private val plantings: List<Planting>,
) : Template {
    override val _filename: String = "harvest_notification.mustache"

    @Suppress("unused")
    val tPlantings = plantings.map {
        PlantingTemplate(
            plantName = it.plant.name,
            expectedYield = it.plantingFormattedTotalYield(),
            plantedOn = it.plantingFormattedDate()
        )
    }

    fun Planting.plantingFormattedTotalYield(): String =
        "${totalYield.from.formatted()}-${totalYield.to.formatted()}${totalYield.unit.string()} (${areaSqM} m²)"

    fun Planting.plantingFormattedDate(): String = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        .withZone(ZoneId.of("UTC"))
        .format(Instant.ofEpochMilli(createdAt))

    private fun Float.formatted(): String = "%.${if (this % 1 == 0f) 0 else 1}f".format(this)

    private fun YieldUnit.string() = when (this) {
        YieldUnit.UNIT -> " units"
        YieldUnit.KG -> " kg"
        YieldUnit.GRAMS -> "g"
        YieldUnit.LB -> " lb"
        YieldUnit.OZ -> " oz"
    }

    data class PlantingTemplate(
        val plantName: String,
        val expectedYield: String,
        val plantedOn: String,
    )
}

