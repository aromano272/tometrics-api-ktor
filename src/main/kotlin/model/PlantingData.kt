package com.sproutscout.api.model

import com.sproutscout.api.domain.models.Planting
import com.sproutscout.api.service.plantsMap
import java.time.Instant

val plantings = listOfNotNull(
    plantsMap["TOMATO"]?.let { plant ->
        Planting(
            id = 1,
            plant = plant,
            areaSqM = 2,
            totalYield = plant.yieldPerSqM.copy(
                from = plant.yieldPerSqM.from * 2,
                to = plant.yieldPerSqM.to * 2
            ),
            createdAt = Instant.now().toEpochMilli()
        )
    },
    plantsMap["CARROT"]?.let { plant ->
        Planting(
            id = 2,
            plant = plant,
            areaSqM = 1,
            totalYield = plant.yieldPerSqM.copy(
                from = plant.yieldPerSqM.from * 1,
                to = plant.yieldPerSqM.to * 1
            ),
            createdAt = Instant.now().toEpochMilli()
        )
    },
    plantsMap["LETTUCE"]?.let { plant ->
        Planting(
            id = 3,
            plant = plant,
            areaSqM = 1,
            totalYield = plant.yieldPerSqM.copy(
                from = plant.yieldPerSqM.from * 1,
                to = plant.yieldPerSqM.to * 1
            ),
            createdAt = Instant.now().toEpochMilli()
        )
    },
    plantsMap["CUCUMBER"]?.let { plant ->
        Planting(
            id = 4,
            plant = plant,
            areaSqM = 3,
            totalYield = plant.yieldPerSqM.copy(
                from = plant.yieldPerSqM.from * 3,
                to = plant.yieldPerSqM.to * 3
            ),
            createdAt = Instant.now().toEpochMilli()
        )
    },
    plantsMap["BELL_PEPPER"]?.let { plant ->
        Planting(
            id = 5,
            plant = plant,
            areaSqM = 2,
            totalYield = plant.yieldPerSqM.copy(
                from = plant.yieldPerSqM.from * 2,
                to = plant.yieldPerSqM.to * 2
            ),
            createdAt = Instant.now().toEpochMilli()
        )
    },
    plantsMap["POTATO"]?.let { plant ->
        Planting(
            id = 6,
            plant = plant,
            areaSqM = 4,
            totalYield = plant.yieldPerSqM.copy(
                from = plant.yieldPerSqM.from * 4,
                to = plant.yieldPerSqM.to * 4
            ),
            createdAt = Instant.now().toEpochMilli()
        )
    },
    plantsMap["BEET"]?.let { plant ->
        Planting(
            id = 7,
            plant = plant,
            areaSqM = 1,
            totalYield = plant.yieldPerSqM.copy(
                from = plant.yieldPerSqM.from * 1,
                to = plant.yieldPerSqM.to * 1
            ),
            createdAt = Instant.now().toEpochMilli()
        )
    },
    plantsMap["RADISH"]?.let { plant ->
        Planting(
            id = 8,
            plant = plant,
            areaSqM = 1,
            totalYield = plant.yieldPerSqM.copy(
                from = plant.yieldPerSqM.from * 1,
                to = plant.yieldPerSqM.to * 1
            ),
            createdAt = Instant.now().toEpochMilli()
        )
    },
    plantsMap["SPINACH"]?.let { plant ->
        Planting(
            id = 9,
            plant = plant,
            areaSqM = 1,
            totalYield = plant.yieldPerSqM.copy(
                from = plant.yieldPerSqM.from * 1,
                to = plant.yieldPerSqM.to * 1
            ),
            createdAt = Instant.now().toEpochMilli()
        )
    },
    plantsMap["KALE"]?.let { plant ->
        Planting(
            id = 10,
            plant = plant,
            areaSqM = 2,
            totalYield = plant.yieldPerSqM.copy(
                from = plant.yieldPerSqM.from * 2,
                to = plant.yieldPerSqM.to * 2
            ),
            createdAt = Instant.now().toEpochMilli()
        )
    },
    plantsMap["GREEN_BEANS"]?.let { plant ->
        Planting(
            id = 11,
            plant = plant,
            areaSqM = 2,
            totalYield = plant.yieldPerSqM.copy(
                from = plant.yieldPerSqM.from * 2,
                to = plant.yieldPerSqM.to * 2
            ),
            createdAt = Instant.now().toEpochMilli()
        )
    },
    plantsMap["PEAS"]?.let { plant ->
        Planting(
            id = 12,
            plant = plant,
            areaSqM = 2,
            totalYield = plant.yieldPerSqM.copy(
                from = plant.yieldPerSqM.from * 2,
                to = plant.yieldPerSqM.to * 2
            ),
            createdAt = Instant.now().toEpochMilli()
        )
    },
    plantsMap["BROCCOLI"]?.let { plant ->
        Planting(
            id = 13,
            plant = plant,
            areaSqM = 2,
            totalYield = plant.yieldPerSqM.copy(
                from = plant.yieldPerSqM.from * 2,
                to = plant.yieldPerSqM.to * 2
            ),
            createdAt = Instant.now().toEpochMilli()
        )
    },
    plantsMap["CAULIFLOWER"]?.let { plant ->
        Planting(
            id = 14,
            plant = plant,
            areaSqM = 2,
            totalYield = plant.yieldPerSqM.copy(
                from = plant.yieldPerSqM.from * 2,
                to = plant.yieldPerSqM.to * 2
            ),
            createdAt = Instant.now().toEpochMilli()
        )
    },
    plantsMap["CABBAGE"]?.let { plant ->
        Planting(
            id = 15,
            plant = plant,
            areaSqM = 2,
            totalYield = plant.yieldPerSqM.copy(
                from = plant.yieldPerSqM.from * 2,
                to = plant.yieldPerSqM.to * 2
            ),
            createdAt = Instant.now().toEpochMilli()
        )
    },
    plantsMap["ONION"]?.let { plant ->
        Planting(
            id = 16,
            plant = plant,
            areaSqM = 1,
            totalYield = plant.yieldPerSqM.copy(
                from = plant.yieldPerSqM.from * 1,
                to = plant.yieldPerSqM.to * 1
            ),
            createdAt = Instant.now().toEpochMilli()
        )
    },
    plantsMap["GARLIC"]?.let { plant ->
        Planting(
            id = 17,
            plant = plant,
            areaSqM = 1,
            totalYield = plant.yieldPerSqM.copy(
                from = plant.yieldPerSqM.from * 1,
                to = plant.yieldPerSqM.to * 1
            ),
            createdAt = Instant.now().toEpochMilli()
        )
    },
    plantsMap["ZUCCHINI"]?.let { plant ->
        Planting(
            id = 18,
            plant = plant,
            areaSqM = 3,
            totalYield = plant.yieldPerSqM.copy(
                from = plant.yieldPerSqM.from * 3,
                to = plant.yieldPerSqM.to * 3
            ),
            createdAt = Instant.now().toEpochMilli()
        )
    },
    plantsMap["PUMPKIN"]?.let { plant ->
        Planting(
            id = 19,
            plant = plant,
            areaSqM = 4,
            totalYield = plant.yieldPerSqM.copy(
                from = plant.yieldPerSqM.from * 4,
                to = plant.yieldPerSqM.to * 4
            ),
            createdAt = Instant.now().toEpochMilli()
        )
    }
)