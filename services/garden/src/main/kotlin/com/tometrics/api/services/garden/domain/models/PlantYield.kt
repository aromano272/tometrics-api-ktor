package com.tometrics.api.services.garden.domain.models

import kotlinx.serialization.Serializable

// TODO(aromano): implement
// create DisplayPlantYield which in turn requires to create DisplayPlant
// displayplantyield should hold all of this convertions and converting from
// domain plantyield to display should convert the values as well

@Serializable
data class PlantYield(
    val from: Float,
    val to: Float,
    val unit: YieldUnit,
) {
    operator fun times(n: Int): PlantYield = PlantYield(
        from = from * n,
        to = to * n,
        unit = unit
    )

    operator fun times(n: Float): PlantYield = PlantYield(
        from = from * n,
        to = to * n,
        unit = unit
    )

    operator fun div(n: Float): PlantYield = PlantYield(
        from = from / n,
        to = to / n,
        unit = unit
    )

    operator fun plus(other: PlantYield): PlantYield {
        if (unit != other.unit) throw IllegalArgumentException("Cannot add 2 PlantYield with different `unit`s")
        return PlantYield(
            from = from + other.from,
            to = to + other.to,
            unit = unit,
        )
    }

    fun asDisplayMetric(): PlantYield {
        val kgs = when (unit) {
            YieldUnit.UNIT -> return this
            YieldUnit.KG -> this
            YieldUnit.GRAMS -> (this * 1000).copy(unit = YieldUnit.KG)
            YieldUnit.LB -> (this / 2.20462f).copy(unit = YieldUnit.KG)
            YieldUnit.OZ -> (this / 35.274f).copy(unit = YieldUnit.KG)
        }

        return if (kgs.to < 1f) {
            (kgs * 1000).copy(unit = YieldUnit.GRAMS)
        } else {
            kgs
        }
    }

    fun asDisplayImperial(): PlantYield {
        val lbs = when (unit) {
            YieldUnit.UNIT -> return this
            YieldUnit.LB -> this
            YieldUnit.OZ -> this * 16
            YieldUnit.KG -> (this * 2.20462f).copy(unit = YieldUnit.LB)
            YieldUnit.GRAMS -> (this / 1000f * 2.20462f).copy(unit = YieldUnit.LB)
        }

        return if (lbs.to < 1.5f) {
            (lbs * 16).copy(unit = YieldUnit.OZ)
        } else {
            lbs
        }
    }

}