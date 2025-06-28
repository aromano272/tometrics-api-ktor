package com.tometrics.api.services.email.services.templates

import com.tometrics.api.services.commonclient.models.garden.GrpcPlanting
import com.tometrics.api.services.commonclient.models.garden.GrpcYieldUnit
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class HarvestNotificationTemplate(
    private val plantings: List<GrpcPlanting>,
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

    fun GrpcPlanting.plantingFormattedTotalYield(): String =
        "${totalYield.from.formatted()}-${totalYield.to.formatted()}${totalYield.unit.string()} (${areaSqM} m²)"

    fun GrpcPlanting.plantingFormattedDate(): String = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        .withZone(ZoneId.of("UTC"))
        .format(Instant.ofEpochMilli(createdAt))

    private fun Float.formatted(): String = "%.${if (this % 1 == 0f) 0 else 1}f".format(this)

    private fun GrpcYieldUnit.string() = when (this) {
        GrpcYieldUnit.UNIT -> " units"
        GrpcYieldUnit.KG -> " kg"
        GrpcYieldUnit.GRAMS -> "g"
        GrpcYieldUnit.LB -> " lb"
        GrpcYieldUnit.OZ -> " oz"
    }

    data class PlantingTemplate(
        val plantName: String,
        val expectedYield: String,
        val plantedOn: String,
    )
}

