package com.sproutscout.api.model

import com.sproutscout.api.routes.Planting
import java.time.Instant

val plantings = listOf(
    Planting(
        id = 1,
        plant = Vegetable.TOMATO.plant,
        plantingAreaM2 = 2,
        totalYield = Vegetable.TOMATO.plant.yieldPerAreaM2.copy(
            from = Vegetable.TOMATO.plant.yieldPerAreaM2.from * 2,
            to = Vegetable.TOMATO.plant.yieldPerAreaM2.to * 2
        ),
        createdAt = Instant.now().toEpochMilli()
    ),
    Planting(
        id = 2,
        plant = Vegetable.CARROT.plant,
        plantingAreaM2 = 1,
        totalYield = Vegetable.CARROT.plant.yieldPerAreaM2.copy(
            from = Vegetable.CARROT.plant.yieldPerAreaM2.from * 1,
            to = Vegetable.CARROT.plant.yieldPerAreaM2.to * 1
        ),
        createdAt = Instant.now().toEpochMilli()
    ),
    Planting(
        id = 3,
        plant = Vegetable.LETTUCE.plant,
        plantingAreaM2 = 1,
        totalYield = Vegetable.LETTUCE.plant.yieldPerAreaM2.copy(
            from = Vegetable.LETTUCE.plant.yieldPerAreaM2.from * 1,
            to = Vegetable.LETTUCE.plant.yieldPerAreaM2.to * 1
        ),
        createdAt = Instant.now().toEpochMilli()
    ),
    Planting(
        id = 4,
        plant = Vegetable.CUCUMBER.plant,
        plantingAreaM2 = 3,
        totalYield = Vegetable.CUCUMBER.plant.yieldPerAreaM2.copy(
            from = Vegetable.CUCUMBER.plant.yieldPerAreaM2.from * 3,
            to = Vegetable.CUCUMBER.plant.yieldPerAreaM2.to * 3
        ),
        createdAt = Instant.now().toEpochMilli()
    ),
    Planting(
        id = 5,
        plant = Vegetable.BELL_PEPPER.plant,
        plantingAreaM2 = 2,
        totalYield = Vegetable.BELL_PEPPER.plant.yieldPerAreaM2.copy(
            from = Vegetable.BELL_PEPPER.plant.yieldPerAreaM2.from * 2,
            to = Vegetable.BELL_PEPPER.plant.yieldPerAreaM2.to * 2
        ),
        createdAt = Instant.now().toEpochMilli()
    ),
    Planting(
        id = 6,
        plant = Vegetable.POTATO.plant,
        plantingAreaM2 = 4,
        totalYield = Vegetable.POTATO.plant.yieldPerAreaM2.copy(
            from = Vegetable.POTATO.plant.yieldPerAreaM2.from * 4,
            to = Vegetable.POTATO.plant.yieldPerAreaM2.to * 4
        ),
        createdAt = Instant.now().toEpochMilli()
    ),
    Planting(
        id = 7,
        plant = Vegetable.BEET.plant,
        plantingAreaM2 = 1,
        totalYield = Vegetable.BEET.plant.yieldPerAreaM2.copy(
            from = Vegetable.BEET.plant.yieldPerAreaM2.from * 1,
            to = Vegetable.BEET.plant.yieldPerAreaM2.to * 1
        ),
        createdAt = Instant.now().toEpochMilli()
    ),
    Planting(
        id = 8,
        plant = Vegetable.RADISH.plant,
        plantingAreaM2 = 1,
        totalYield = Vegetable.RADISH.plant.yieldPerAreaM2.copy(
            from = Vegetable.RADISH.plant.yieldPerAreaM2.from * 1,
            to = Vegetable.RADISH.plant.yieldPerAreaM2.to * 1
        ),
        createdAt = Instant.now().toEpochMilli()
    ),
    Planting(
        id = 9,
        plant = Vegetable.SPINACH.plant,
        plantingAreaM2 = 1,
        totalYield = Vegetable.SPINACH.plant.yieldPerAreaM2.copy(
            from = Vegetable.SPINACH.plant.yieldPerAreaM2.from * 1,
            to = Vegetable.SPINACH.plant.yieldPerAreaM2.to * 1
        ),
        createdAt = Instant.now().toEpochMilli()
    ),
    Planting(
        id = 10,
        plant = Vegetable.KALE.plant,
        plantingAreaM2 = 2,
        totalYield = Vegetable.KALE.plant.yieldPerAreaM2.copy(
            from = Vegetable.KALE.plant.yieldPerAreaM2.from * 2,
            to = Vegetable.KALE.plant.yieldPerAreaM2.to * 2
        ),
        createdAt = Instant.now().toEpochMilli()
    ),
    Planting(
        id = 11,
        plant = Vegetable.GREEN_BEANS.plant,
        plantingAreaM2 = 2,
        totalYield = Vegetable.GREEN_BEANS.plant.yieldPerAreaM2.copy(
            from = Vegetable.GREEN_BEANS.plant.yieldPerAreaM2.from * 2,
            to = Vegetable.GREEN_BEANS.plant.yieldPerAreaM2.to * 2
        ),
        createdAt = Instant.now().toEpochMilli()
    ),
    Planting(
        id = 12,
        plant = Vegetable.PEAS.plant,
        plantingAreaM2 = 2,
        totalYield = Vegetable.PEAS.plant.yieldPerAreaM2.copy(
            from = Vegetable.PEAS.plant.yieldPerAreaM2.from * 2,
            to = Vegetable.PEAS.plant.yieldPerAreaM2.to * 2
        ),
        createdAt = Instant.now().toEpochMilli()
    ),
    Planting(
        id = 13,
        plant = Vegetable.BROCCOLI.plant,
        plantingAreaM2 = 2,
        totalYield = Vegetable.BROCCOLI.plant.yieldPerAreaM2.copy(
            from = Vegetable.BROCCOLI.plant.yieldPerAreaM2.from * 2,
            to = Vegetable.BROCCOLI.plant.yieldPerAreaM2.to * 2
        ),
        createdAt = Instant.now().toEpochMilli()
    ),
    Planting(
        id = 14,
        plant = Vegetable.CAULIFLOWER.plant,
        plantingAreaM2 = 2,
        totalYield = Vegetable.CAULIFLOWER.plant.yieldPerAreaM2.copy(
            from = Vegetable.CAULIFLOWER.plant.yieldPerAreaM2.from * 2,
            to = Vegetable.CAULIFLOWER.plant.yieldPerAreaM2.to * 2
        ),
        createdAt = Instant.now().toEpochMilli()
    ),
    Planting(
        id = 15,
        plant = Vegetable.CABBAGE.plant,
        plantingAreaM2 = 2,
        totalYield = Vegetable.CABBAGE.plant.yieldPerAreaM2.copy(
            from = Vegetable.CABBAGE.plant.yieldPerAreaM2.from * 2,
            to = Vegetable.CABBAGE.plant.yieldPerAreaM2.to * 2
        ),
        createdAt = Instant.now().toEpochMilli()
    ),
    Planting(
        id = 16,
        plant = Vegetable.ONION.plant,
        plantingAreaM2 = 1,
        totalYield = Vegetable.ONION.plant.yieldPerAreaM2.copy(
            from = Vegetable.ONION.plant.yieldPerAreaM2.from * 1,
            to = Vegetable.ONION.plant.yieldPerAreaM2.to * 1
        ),
        createdAt = Instant.now().toEpochMilli()
    ),
    Planting(
        id = 17,
        plant = Vegetable.GARLIC.plant,
        plantingAreaM2 = 1,
        totalYield = Vegetable.GARLIC.plant.yieldPerAreaM2.copy(
            from = Vegetable.GARLIC.plant.yieldPerAreaM2.from * 1,
            to = Vegetable.GARLIC.plant.yieldPerAreaM2.to * 1
        ),
        createdAt = Instant.now().toEpochMilli()
    ),
    Planting(
        id = 18,
        plant = Vegetable.ZUCCHINI.plant,
        plantingAreaM2 = 3,
        totalYield = Vegetable.ZUCCHINI.plant.yieldPerAreaM2.copy(
            from = Vegetable.ZUCCHINI.plant.yieldPerAreaM2.from * 3,
            to = Vegetable.ZUCCHINI.plant.yieldPerAreaM2.to * 3
        ),
        createdAt = Instant.now().toEpochMilli()
    ),
    Planting(
        id = 19,
        plant = Vegetable.PUMPKIN.plant,
        plantingAreaM2 = 4,
        totalYield = Vegetable.PUMPKIN.plant.yieldPerAreaM2.copy(
            from = Vegetable.PUMPKIN.plant.yieldPerAreaM2.from * 4,
            to = Vegetable.PUMPKIN.plant.yieldPerAreaM2.to * 4
        ),
        createdAt = Instant.now().toEpochMilli()
    )
)