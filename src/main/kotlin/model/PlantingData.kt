//package com.sproutscout.api.model
//
//import com.sproutscout.api.routes.Planting
//import java.time.Instant
//
//val plantings = listOf(
//    Planting(
//        id = 1,
//        plant = Plant.TOMATO.plant,
//        plantingAreaM2 = 2,
//        totalYield = Plant.TOMATO.plant.yieldPerAreaM2.copy(
//            from = Plant.TOMATO.plant.yieldPerAreaM2.from * 2,
//            to = Plant.TOMATO.plant.yieldPerAreaM2.to * 2
//        ),
//        createdAt = Instant.now().toEpochMilli()
//    ),
//    Planting(
//        id = 2,
//        plant = Plant.CARROT.plant,
//        plantingAreaM2 = 1,
//        totalYield = Plant.CARROT.plant.yieldPerAreaM2.copy(
//            from = Plant.CARROT.plant.yieldPerAreaM2.from * 1,
//            to = Plant.CARROT.plant.yieldPerAreaM2.to * 1
//        ),
//        createdAt = Instant.now().toEpochMilli()
//    ),
//    Planting(
//        id = 3,
//        plant = Plant.LETTUCE.plant,
//        plantingAreaM2 = 1,
//        totalYield = Plant.LETTUCE.plant.yieldPerAreaM2.copy(
//            from = Plant.LETTUCE.plant.yieldPerAreaM2.from * 1,
//            to = Plant.LETTUCE.plant.yieldPerAreaM2.to * 1
//        ),
//        createdAt = Instant.now().toEpochMilli()
//    ),
//    Planting(
//        id = 4,
//        plant = Plant.CUCUMBER.plant,
//        plantingAreaM2 = 3,
//        totalYield = Plant.CUCUMBER.plant.yieldPerAreaM2.copy(
//            from = Plant.CUCUMBER.plant.yieldPerAreaM2.from * 3,
//            to = Plant.CUCUMBER.plant.yieldPerAreaM2.to * 3
//        ),
//        createdAt = Instant.now().toEpochMilli()
//    ),
//    Planting(
//        id = 5,
//        plant = Plant.BELL_PEPPER.plant,
//        plantingAreaM2 = 2,
//        totalYield = Plant.BELL_PEPPER.plant.yieldPerAreaM2.copy(
//            from = Plant.BELL_PEPPER.plant.yieldPerAreaM2.from * 2,
//            to = Plant.BELL_PEPPER.plant.yieldPerAreaM2.to * 2
//        ),
//        createdAt = Instant.now().toEpochMilli()
//    ),
//    Planting(
//        id = 6,
//        plant = Plant.POTATO.plant,
//        plantingAreaM2 = 4,
//        totalYield = Plant.POTATO.plant.yieldPerAreaM2.copy(
//            from = Plant.POTATO.plant.yieldPerAreaM2.from * 4,
//            to = Plant.POTATO.plant.yieldPerAreaM2.to * 4
//        ),
//        createdAt = Instant.now().toEpochMilli()
//    ),
//    Planting(
//        id = 7,
//        plant = Plant.BEET.plant,
//        plantingAreaM2 = 1,
//        totalYield = Plant.BEET.plant.yieldPerAreaM2.copy(
//            from = Plant.BEET.plant.yieldPerAreaM2.from * 1,
//            to = Plant.BEET.plant.yieldPerAreaM2.to * 1
//        ),
//        createdAt = Instant.now().toEpochMilli()
//    ),
//    Planting(
//        id = 8,
//        plant = Plant.RADISH.plant,
//        plantingAreaM2 = 1,
//        totalYield = Plant.RADISH.plant.yieldPerAreaM2.copy(
//            from = Plant.RADISH.plant.yieldPerAreaM2.from * 1,
//            to = Plant.RADISH.plant.yieldPerAreaM2.to * 1
//        ),
//        createdAt = Instant.now().toEpochMilli()
//    ),
//    Planting(
//        id = 9,
//        plant = Plant.SPINACH.plant,
//        plantingAreaM2 = 1,
//        totalYield = Plant.SPINACH.plant.yieldPerAreaM2.copy(
//            from = Plant.SPINACH.plant.yieldPerAreaM2.from * 1,
//            to = Plant.SPINACH.plant.yieldPerAreaM2.to * 1
//        ),
//        createdAt = Instant.now().toEpochMilli()
//    ),
//    Planting(
//        id = 10,
//        plant = Plant.KALE.plant,
//        plantingAreaM2 = 2,
//        totalYield = Plant.KALE.plant.yieldPerAreaM2.copy(
//            from = Plant.KALE.plant.yieldPerAreaM2.from * 2,
//            to = Plant.KALE.plant.yieldPerAreaM2.to * 2
//        ),
//        createdAt = Instant.now().toEpochMilli()
//    ),
//    Planting(
//        id = 11,
//        plant = Plant.GREEN_BEANS.plant,
//        plantingAreaM2 = 2,
//        totalYield = Plant.GREEN_BEANS.plant.yieldPerAreaM2.copy(
//            from = Plant.GREEN_BEANS.plant.yieldPerAreaM2.from * 2,
//            to = Plant.GREEN_BEANS.plant.yieldPerAreaM2.to * 2
//        ),
//        createdAt = Instant.now().toEpochMilli()
//    ),
//    Planting(
//        id = 12,
//        plant = Plant.PEAS.plant,
//        plantingAreaM2 = 2,
//        totalYield = Plant.PEAS.plant.yieldPerAreaM2.copy(
//            from = Plant.PEAS.plant.yieldPerAreaM2.from * 2,
//            to = Plant.PEAS.plant.yieldPerAreaM2.to * 2
//        ),
//        createdAt = Instant.now().toEpochMilli()
//    ),
//    Planting(
//        id = 13,
//        plant = Plant.BROCCOLI.plant,
//        plantingAreaM2 = 2,
//        totalYield = Plant.BROCCOLI.plant.yieldPerAreaM2.copy(
//            from = Plant.BROCCOLI.plant.yieldPerAreaM2.from * 2,
//            to = Plant.BROCCOLI.plant.yieldPerAreaM2.to * 2
//        ),
//        createdAt = Instant.now().toEpochMilli()
//    ),
//    Planting(
//        id = 14,
//        plant = Plant.CAULIFLOWER.plant,
//        plantingAreaM2 = 2,
//        totalYield = Plant.CAULIFLOWER.plant.yieldPerAreaM2.copy(
//            from = Plant.CAULIFLOWER.plant.yieldPerAreaM2.from * 2,
//            to = Plant.CAULIFLOWER.plant.yieldPerAreaM2.to * 2
//        ),
//        createdAt = Instant.now().toEpochMilli()
//    ),
//    Planting(
//        id = 15,
//        plant = Plant.CABBAGE.plant,
//        plantingAreaM2 = 2,
//        totalYield = Plant.CABBAGE.plant.yieldPerAreaM2.copy(
//            from = Plant.CABBAGE.plant.yieldPerAreaM2.from * 2,
//            to = Plant.CABBAGE.plant.yieldPerAreaM2.to * 2
//        ),
//        createdAt = Instant.now().toEpochMilli()
//    ),
//    Planting(
//        id = 16,
//        plant = Plant.ONION.plant,
//        plantingAreaM2 = 1,
//        totalYield = Plant.ONION.plant.yieldPerAreaM2.copy(
//            from = Plant.ONION.plant.yieldPerAreaM2.from * 1,
//            to = Plant.ONION.plant.yieldPerAreaM2.to * 1
//        ),
//        createdAt = Instant.now().toEpochMilli()
//    ),
//    Planting(
//        id = 17,
//        plant = Plant.GARLIC.plant,
//        plantingAreaM2 = 1,
//        totalYield = Plant.GARLIC.plant.yieldPerAreaM2.copy(
//            from = Plant.GARLIC.plant.yieldPerAreaM2.from * 1,
//            to = Plant.GARLIC.plant.yieldPerAreaM2.to * 1
//        ),
//        createdAt = Instant.now().toEpochMilli()
//    ),
//    Planting(
//        id = 18,
//        plant = Plant.ZUCCHINI.plant,
//        plantingAreaM2 = 3,
//        totalYield = Plant.ZUCCHINI.plant.yieldPerAreaM2.copy(
//            from = Plant.ZUCCHINI.plant.yieldPerAreaM2.from * 3,
//            to = Plant.ZUCCHINI.plant.yieldPerAreaM2.to * 3
//        ),
//        createdAt = Instant.now().toEpochMilli()
//    ),
//    Planting(
//        id = 19,
//        plant = Plant.PUMPKIN.plant,
//        plantingAreaM2 = 4,
//        totalYield = Plant.PUMPKIN.plant.yieldPerAreaM2.copy(
//            from = Plant.PUMPKIN.plant.yieldPerAreaM2.from * 4,
//            to = Plant.PUMPKIN.plant.yieldPerAreaM2.to * 4
//        ),
//        createdAt = Instant.now().toEpochMilli()
//    )
//)