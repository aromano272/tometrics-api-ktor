package com.tometrics.api.services.garden.services

import com.tometrics.api.services.commongrpc.models.garden.*
import com.tometrics.api.services.garden.domain.models.ClimateZones
import com.tometrics.api.services.garden.domain.models.DailySunlightRequirement
import com.tometrics.api.services.garden.domain.models.GrowingTip
import com.tometrics.api.services.garden.domain.models.GrowthHabit
import com.tometrics.api.services.garden.domain.models.Plant
import com.tometrics.api.services.garden.domain.models.PlantYield
import com.tometrics.api.services.garden.domain.models.Planting
import com.tometrics.api.services.garden.domain.models.SoilType
import com.tometrics.api.services.garden.domain.models.SpacingRequirement
import com.tometrics.api.services.garden.domain.models.SunlightRequirement
import com.tometrics.api.services.garden.domain.models.WaterRequirement
import com.tometrics.api.services.garden.domain.models.YieldUnit
import com.tometrics.api.services.protos.*

class DefaultGardenGrpcService(
    private val service: GardenService,
) : GardenGrpcServiceGrpcKt.GardenGrpcServiceCoroutineImplBase() {
    override suspend fun getAllReadyForHarvestToday(request: Empty): GetAllReadyForHarvestTodayResponse =
        service.getAllReadyForHarvestToday().let { response ->
            getAllReadyForHarvestTodayResponse {
                val network = response.mapValues { (_, plantings) ->
                    GetAllReadyForHarvestTodayResponseKt.listPlanting {
                        planting += plantings.map { it.toGrpc().toNetwork() }
                    }
                }
                results.putAll(network)
            }
        }

}

// NOTE(aromano): These mappings seems needless at first glance, still unsure if it's worth, but ie. the alternative
// would be to put the domain.Planting in commongrpc for every service to know, which doesn't sound too good
private fun Planting.toGrpc() = GrpcPlanting(
    id = id,
    name = name,
    plant = plant.toGrpc(),
    areaSqM = areaSqM,
    totalYield = totalYield.toGrpc(),
    quantity = quantity,
    createdAt = createdAt,
    readyToHarvestAt = readyToHarvestAt,
    diary = diary,
    harvested = harvested,
)

private fun Plant.toGrpc() = GrpcPlant(
    id = id,
    name = name,
    timeToHarvest = timeToHarvest,
    yieldPerPlant = yieldPerPlant.toGrpc(),
    yieldPerSqM = yieldPerSqM.toGrpc(),
    companionPlants = companionPlants,
    climateZones = climateZones.toGrpc(),
    spacing = spacing.toGrpc(),
    sunlight = sunlight.toGrpc(),
    dailySunlight = dailySunlight.toGrpc(),
    soilType = soilType.map { it.toGrpc() },
    waterRequirement = waterRequirement.toGrpc(),
    growthHabit = growthHabit.toGrpc(),
    growingTips = growingTips.map { it.toGrpc() },
)

private fun PlantYield.toGrpc() = GrpcPlantYield(
    from = from,
    to = to,
    unit = unit.toGrpc()
)

private fun com.tometrics.api.services.garden.domain.models.YieldUnit.toGrpc() = when (this) {
    com.tometrics.api.services.garden.domain.models.YieldUnit.UNIT -> GrpcYieldUnit.UNIT
    com.tometrics.api.services.garden.domain.models.YieldUnit.KG -> GrpcYieldUnit.KG
    com.tometrics.api.services.garden.domain.models.YieldUnit.GRAMS -> GrpcYieldUnit.GRAMS
    com.tometrics.api.services.garden.domain.models.YieldUnit.LB -> GrpcYieldUnit.LB
    YieldUnit.OZ -> GrpcYieldUnit.OZ
}

private fun ClimateZones.toGrpc() = GrpcClimateZones(
    temperate = temperate,
    mediterranean = mediterranean,
    continental = continental,
    tropical = tropical,
    arid = arid
)

private fun com.tometrics.api.services.garden.domain.models.SpacingRequirement.toGrpc() = when (this) {
    com.tometrics.api.services.garden.domain.models.SpacingRequirement.VERY_CLOSE -> GrpcSpacingRequirement.VERY_CLOSE
    com.tometrics.api.services.garden.domain.models.SpacingRequirement.CLOSE -> GrpcSpacingRequirement.CLOSE
    com.tometrics.api.services.garden.domain.models.SpacingRequirement.MODERATE -> GrpcSpacingRequirement.MODERATE
    com.tometrics.api.services.garden.domain.models.SpacingRequirement.WIDE -> GrpcSpacingRequirement.WIDE
    SpacingRequirement.VERY_WIDE -> GrpcSpacingRequirement.VERY_WIDE
}

private fun com.tometrics.api.services.garden.domain.models.SunlightRequirement.toGrpc() = when (this) {
    com.tometrics.api.services.garden.domain.models.SunlightRequirement.FULL_SUN -> GrpcSunlightRequirement.FULL_SUN
    com.tometrics.api.services.garden.domain.models.SunlightRequirement.PARTIAL_SUN -> GrpcSunlightRequirement.PARTIAL_SUN
    com.tometrics.api.services.garden.domain.models.SunlightRequirement.PARTIAL_SHADE -> GrpcSunlightRequirement.PARTIAL_SHADE
    SunlightRequirement.FULL_SHADE -> GrpcSunlightRequirement.FULL_SHADE
}

private fun com.tometrics.api.services.garden.domain.models.DailySunlightRequirement.toGrpc() = when (this) {
    com.tometrics.api.services.garden.domain.models.DailySunlightRequirement.LOW -> GrpcDailySunlightRequirement.LOW
    com.tometrics.api.services.garden.domain.models.DailySunlightRequirement.MEDIUM -> GrpcDailySunlightRequirement.MEDIUM
    DailySunlightRequirement.HIGH -> GrpcDailySunlightRequirement.HIGH
}

private fun com.tometrics.api.services.garden.domain.models.SoilType.toGrpc() = when (this) {
    com.tometrics.api.services.garden.domain.models.SoilType.SANDY -> GrpcSoilType.SANDY
    com.tometrics.api.services.garden.domain.models.SoilType.LOAMY -> GrpcSoilType.LOAMY
    com.tometrics.api.services.garden.domain.models.SoilType.CLAY -> GrpcSoilType.CLAY
    com.tometrics.api.services.garden.domain.models.SoilType.PEATY -> GrpcSoilType.PEATY
    com.tometrics.api.services.garden.domain.models.SoilType.CHALKY -> GrpcSoilType.CHALKY
    SoilType.SILTY -> GrpcSoilType.SILTY
}

private fun com.tometrics.api.services.garden.domain.models.WaterRequirement.toGrpc() = when (this) {
    com.tometrics.api.services.garden.domain.models.WaterRequirement.LOW -> GrpcWaterRequirement.LOW
    com.tometrics.api.services.garden.domain.models.WaterRequirement.MEDIUM -> GrpcWaterRequirement.MEDIUM
    WaterRequirement.HIGH -> GrpcWaterRequirement.HIGH
}

private fun com.tometrics.api.services.garden.domain.models.GrowthHabit.toGrpc() = when (this) {
    com.tometrics.api.services.garden.domain.models.GrowthHabit.DETERMINATE -> GrpcGrowthHabit.DETERMINATE
    com.tometrics.api.services.garden.domain.models.GrowthHabit.INDETERMINATE -> GrpcGrowthHabit.INDETERMINATE
    com.tometrics.api.services.garden.domain.models.GrowthHabit.BUSH -> GrpcGrowthHabit.BUSH
    com.tometrics.api.services.garden.domain.models.GrowthHabit.VINING -> GrpcGrowthHabit.VINING
    com.tometrics.api.services.garden.domain.models.GrowthHabit.UPRIGHT -> GrpcGrowthHabit.UPRIGHT
    com.tometrics.api.services.garden.domain.models.GrowthHabit.ROSETTE -> GrpcGrowthHabit.ROSETTE
    com.tometrics.api.services.garden.domain.models.GrowthHabit.CLUMPING -> GrpcGrowthHabit.CLUMPING
    com.tometrics.api.services.garden.domain.models.GrowthHabit.SPREADING -> GrpcGrowthHabit.SPREADING
    GrowthHabit.MOUNDING -> GrpcGrowthHabit.MOUNDING
}

private fun com.tometrics.api.services.garden.domain.models.GrowingTip.toGrpc() = when (this) {
    com.tometrics.api.services.garden.domain.models.GrowingTip.PRUNE_SUCKERS -> GrpcGrowingTip.PRUNE_SUCKERS
    com.tometrics.api.services.garden.domain.models.GrowingTip.PROVIDE_SUPPORT -> GrpcGrowingTip.PROVIDE_SUPPORT
    com.tometrics.api.services.garden.domain.models.GrowingTip.MULCH_SOIL -> GrpcGrowingTip.MULCH_SOIL
    com.tometrics.api.services.garden.domain.models.GrowingTip.WATER_AT_BASE -> GrpcGrowingTip.WATER_AT_BASE
    com.tometrics.api.services.garden.domain.models.GrowingTip.CROP_ROTATION -> GrpcGrowingTip.CROP_ROTATION
    com.tometrics.api.services.garden.domain.models.GrowingTip.HILL_SOIL -> GrpcGrowingTip.HILL_SOIL
    com.tometrics.api.services.garden.domain.models.GrowingTip.PLANT_DEEP -> GrpcGrowingTip.PLANT_DEEP
    com.tometrics.api.services.garden.domain.models.GrowingTip.AVOID_LIGHT_EXPOSURE -> GrpcGrowingTip.AVOID_LIGHT_EXPOSURE
    com.tometrics.api.services.garden.domain.models.GrowingTip.HARVEST_AFTER_FLOWERING -> GrpcGrowingTip.HARVEST_AFTER_FLOWERING
    com.tometrics.api.services.garden.domain.models.GrowingTip.COOL_STORAGE -> GrpcGrowingTip.COOL_STORAGE
    com.tometrics.api.services.garden.domain.models.GrowingTip.LOOSEN_SOIL -> GrpcGrowingTip.LOOSEN_SOIL
    com.tometrics.api.services.garden.domain.models.GrowingTip.THIN_SEEDLINGS -> GrpcGrowingTip.THIN_SEEDLINGS
    com.tometrics.api.services.garden.domain.models.GrowingTip.CONSISTENT_MOISTURE -> GrpcGrowingTip.CONSISTENT_MOISTURE
    com.tometrics.api.services.garden.domain.models.GrowingTip.AVOID_ROCKY_SOIL -> GrpcGrowingTip.AVOID_ROCKY_SOIL
    com.tometrics.api.services.garden.domain.models.GrowingTip.PREVENT_GREENING -> GrpcGrowingTip.PREVENT_GREENING
    com.tometrics.api.services.garden.domain.models.GrowingTip.PLANT_TIPS_UP -> GrpcGrowingTip.PLANT_TIPS_UP
    com.tometrics.api.services.garden.domain.models.GrowingTip.REDUCE_WATERING_AT_MATURITY -> GrpcGrowingTip.REDUCE_WATERING_AT_MATURITY
    com.tometrics.api.services.garden.domain.models.GrowingTip.CURE_AFTER_HARVEST -> GrpcGrowingTip.CURE_AFTER_HARVEST
    com.tometrics.api.services.garden.domain.models.GrowingTip.DAY_LENGTH_SENSITIVE -> GrpcGrowingTip.DAY_LENGTH_SENSITIVE
    com.tometrics.api.services.garden.domain.models.GrowingTip.SUPPORT_WHEN_FRUITING -> GrpcGrowingTip.SUPPORT_WHEN_FRUITING
    com.tometrics.api.services.garden.domain.models.GrowingTip.HARVEST_WHEN_FIRM -> GrpcGrowingTip.HARVEST_WHEN_FIRM
    com.tometrics.api.services.garden.domain.models.GrowingTip.PREVENT_BLOSSOM_END_ROT -> GrpcGrowingTip.PREVENT_BLOSSOM_END_ROT
    com.tometrics.api.services.garden.domain.models.GrowingTip.PRODUCES_UNTIL_FROST -> GrpcGrowingTip.PRODUCES_UNTIL_FROST
    com.tometrics.api.services.garden.domain.models.GrowingTip.SUCCESSION_PLANTING -> GrpcGrowingTip.SUCCESSION_PLANTING
    com.tometrics.api.services.garden.domain.models.GrowingTip.DIRECT_SOW -> GrpcGrowingTip.DIRECT_SOW
    com.tometrics.api.services.garden.domain.models.GrowingTip.COLD_HARDY -> GrpcGrowingTip.COLD_HARDY
    com.tometrics.api.services.garden.domain.models.GrowingTip.HEAT_SENSITIVE -> GrpcGrowingTip.HEAT_SENSITIVE
    com.tometrics.api.services.garden.domain.models.GrowingTip.HEAT_TOLERANT -> GrpcGrowingTip.HEAT_TOLERANT
    com.tometrics.api.services.garden.domain.models.GrowingTip.PINCH_FLOWERS -> GrpcGrowingTip.PINCH_FLOWERS
    com.tometrics.api.services.garden.domain.models.GrowingTip.DEADHEAD -> GrpcGrowingTip.DEADHEAD
    com.tometrics.api.services.garden.domain.models.GrowingTip.REQUIRES_POLLINATION -> GrpcGrowingTip.REQUIRES_POLLINATION
    com.tometrics.api.services.garden.domain.models.GrowingTip.HAND_POLLINATE -> GrpcGrowingTip.HAND_POLLINATE
    com.tometrics.api.services.garden.domain.models.GrowingTip.AVOID_OVERHEAD_WATERING -> GrpcGrowingTip.AVOID_OVERHEAD_WATERING
    com.tometrics.api.services.garden.domain.models.GrowingTip.PLANT_IN_BLOCKS -> GrpcGrowingTip.PLANT_IN_BLOCKS
    com.tometrics.api.services.garden.domain.models.GrowingTip.HARVEST_REGULARLY -> GrpcGrowingTip.HARVEST_REGULARLY
    com.tometrics.api.services.garden.domain.models.GrowingTip.BLANCHING -> GrpcGrowingTip.BLANCHING
    com.tometrics.api.services.garden.domain.models.GrowingTip.ACIDIC_SOIL -> GrpcGrowingTip.ACIDIC_SOIL
    com.tometrics.api.services.garden.domain.models.GrowingTip.ALKALINE_SOIL -> GrpcGrowingTip.ALKALINE_SOIL
    com.tometrics.api.services.garden.domain.models.GrowingTip.DEEP_ROOT_SYSTEM -> GrpcGrowingTip.DEEP_ROOT_SYSTEM
    com.tometrics.api.services.garden.domain.models.GrowingTip.SHALLOW_ROOT_SYSTEM -> GrpcGrowingTip.SHALLOW_ROOT_SYSTEM
    com.tometrics.api.services.garden.domain.models.GrowingTip.MOUND_PLANTING -> GrpcGrowingTip.MOUND_PLANTING
    com.tometrics.api.services.garden.domain.models.GrowingTip.TRENCH_PLANTING -> GrpcGrowingTip.TRENCH_PLANTING
    com.tometrics.api.services.garden.domain.models.GrowingTip.REQUIRES_STAKING -> GrpcGrowingTip.REQUIRES_STAKING
    com.tometrics.api.services.garden.domain.models.GrowingTip.NITROGEN_FIXING -> GrpcGrowingTip.NITROGEN_FIXING
    com.tometrics.api.services.garden.domain.models.GrowingTip.CUT_AND_COME_AGAIN -> GrpcGrowingTip.CUT_AND_COME_AGAIN
    com.tometrics.api.services.garden.domain.models.GrowingTip.PERENNIAL_CROP -> GrpcGrowingTip.PERENNIAL_CROP
    com.tometrics.api.services.garden.domain.models.GrowingTip.HARVEST_SELECTIVELY -> GrpcGrowingTip.HARVEST_SELECTIVELY
    com.tometrics.api.services.garden.domain.models.GrowingTip.DROUGHT_RESISTANT -> GrpcGrowingTip.DROUGHT_RESISTANT
    com.tometrics.api.services.garden.domain.models.GrowingTip.AVOID_TRANSPLANTING -> GrpcGrowingTip.AVOID_TRANSPLANTING
    com.tometrics.api.services.garden.domain.models.GrowingTip.COMPANION_BENEFICIAL -> GrpcGrowingTip.COMPANION_BENEFICIAL
    com.tometrics.api.services.garden.domain.models.GrowingTip.SHADE_TOLERANT -> GrpcGrowingTip.SHADE_TOLERANT
    com.tometrics.api.services.garden.domain.models.GrowingTip.EARLY_SPRING_CROP -> GrpcGrowingTip.EARLY_SPRING_CROP
    com.tometrics.api.services.garden.domain.models.GrowingTip.FALL_CROP -> GrpcGrowingTip.FALL_CROP
    com.tometrics.api.services.garden.domain.models.GrowingTip.OVERWINTER -> GrpcGrowingTip.OVERWINTER
    com.tometrics.api.services.garden.domain.models.GrowingTip.COOL_WEATHER_CROP -> GrpcGrowingTip.COOL_WEATHER_CROP
    com.tometrics.api.services.garden.domain.models.GrowingTip.REMOVE_LOWER_LEAVES -> GrpcGrowingTip.REMOVE_LOWER_LEAVES
    com.tometrics.api.services.garden.domain.models.GrowingTip.FERTILE_SOIL -> GrpcGrowingTip.FERTILE_SOIL
    com.tometrics.api.services.garden.domain.models.GrowingTip.NEEDS_SUPPORT_CAGE -> GrpcGrowingTip.NEEDS_SUPPORT_CAGE
    com.tometrics.api.services.garden.domain.models.GrowingTip.FLOATING_ROW_COVER -> GrpcGrowingTip.FLOATING_ROW_COVER
    com.tometrics.api.services.garden.domain.models.GrowingTip.SLOW_TO_GERMINATE -> GrpcGrowingTip.SLOW_TO_GERMINATE
    com.tometrics.api.services.garden.domain.models.GrowingTip.SOAK_SEEDS -> GrpcGrowingTip.SOAK_SEEDS
    com.tometrics.api.services.garden.domain.models.GrowingTip.PINCH_GROWING_TIPS -> GrpcGrowingTip.PINCH_GROWING_TIPS
    com.tometrics.api.services.garden.domain.models.GrowingTip.STRATIFICATION -> GrpcGrowingTip.STRATIFICATION
    com.tometrics.api.services.garden.domain.models.GrowingTip.START_INDOORS -> GrpcGrowingTip.START_INDOORS
    com.tometrics.api.services.garden.domain.models.GrowingTip.LOOSE_SOIL -> GrpcGrowingTip.LOOSE_SOIL
    com.tometrics.api.services.garden.domain.models.GrowingTip.HARVEST_WHEN_SMALL -> GrpcGrowingTip.HARVEST_WHEN_SMALL
    com.tometrics.api.services.garden.domain.models.GrowingTip.MINIMAL_COMPANIONS -> GrpcGrowingTip.MINIMAL_COMPANIONS
    com.tometrics.api.services.garden.domain.models.GrowingTip.FROST_IMPROVES_FLAVOR -> GrpcGrowingTip.FROST_IMPROVES_FLAVOR
    com.tometrics.api.services.garden.domain.models.GrowingTip.AVOID_HARVESTING_FIRST_YEAR -> GrpcGrowingTip.AVOID_HARVESTING_FIRST_YEAR
    com.tometrics.api.services.garden.domain.models.GrowingTip.REMOVE_FLOWER_STALKS -> GrpcGrowingTip.REMOVE_FLOWER_STALKS
    com.tometrics.api.services.garden.domain.models.GrowingTip.DIVIDE_EVERY_FEW_YEARS -> GrpcGrowingTip.DIVIDE_EVERY_FEW_YEARS
    com.tometrics.api.services.garden.domain.models.GrowingTip.DIVIDE_BULBS -> GrpcGrowingTip.DIVIDE_BULBS
    com.tometrics.api.services.garden.domain.models.GrowingTip.NEEDS_CONSTANT_MOISTURE -> GrpcGrowingTip.NEEDS_CONSTANT_MOISTURE
    com.tometrics.api.services.garden.domain.models.GrowingTip.AQUATIC_FRIENDLY -> GrpcGrowingTip.AQUATIC_FRIENDLY
    com.tometrics.api.services.garden.domain.models.GrowingTip.HARVEST_AFTER_FROST -> GrpcGrowingTip.HARVEST_AFTER_FROST
    com.tometrics.api.services.garden.domain.models.GrowingTip.CONTAIN_SPREAD -> GrpcGrowingTip.CONTAIN_SPREAD
    com.tometrics.api.services.garden.domain.models.GrowingTip.NEEDS_SPACE -> GrpcGrowingTip.NEEDS_SPACE
    com.tometrics.api.services.garden.domain.models.GrowingTip.FULL_MATURITY_FOR_STORAGE -> GrpcGrowingTip.FULL_MATURITY_FOR_STORAGE
    com.tometrics.api.services.garden.domain.models.GrowingTip.HARVEST_IN_FALL_OR_SPRING -> GrpcGrowingTip.HARVEST_IN_FALL_OR_SPRING
    com.tometrics.api.services.garden.domain.models.GrowingTip.NEEDS_LONG_SEASON -> GrpcGrowingTip.NEEDS_LONG_SEASON
    com.tometrics.api.services.garden.domain.models.GrowingTip.PROPER_PROCESSING_REQUIRED -> GrpcGrowingTip.PROPER_PROCESSING_REQUIRED
    com.tometrics.api.services.garden.domain.models.GrowingTip.SUBMERGED_PLANTING -> GrpcGrowingTip.SUBMERGED_PLANTING
    com.tometrics.api.services.garden.domain.models.GrowingTip.SPECIAL_GROWING_CONDITIONS -> GrpcGrowingTip.SPECIAL_GROWING_CONDITIONS
    com.tometrics.api.services.garden.domain.models.GrowingTip.SLOW_GROWING -> GrpcGrowingTip.SLOW_GROWING
    com.tometrics.api.services.garden.domain.models.GrowingTip.WAIT_FOR_WARM_SOIL -> GrpcGrowingTip.WAIT_FOR_WARM_SOIL
    com.tometrics.api.services.garden.domain.models.GrowingTip.AVOID_RICH_SOIL -> GrpcGrowingTip.AVOID_RICH_SOIL
    com.tometrics.api.services.garden.domain.models.GrowingTip.SHALLOW_PLANTING -> GrpcGrowingTip.SHALLOW_PLANTING
    com.tometrics.api.services.garden.domain.models.GrowingTip.EDIBLE_ROOTS_ONLY -> GrpcGrowingTip.EDIBLE_ROOTS_ONLY
    com.tometrics.api.services.garden.domain.models.GrowingTip.LONG_GROWING_SEASON -> GrpcGrowingTip.LONG_GROWING_SEASON
    com.tometrics.api.services.garden.domain.models.GrowingTip.PREVENT_FLOWERING -> GrpcGrowingTip.PREVENT_FLOWERING
    com.tometrics.api.services.garden.domain.models.GrowingTip.ACIDIC_FLAVOR -> GrpcGrowingTip.ACIDIC_FLAVOR
    com.tometrics.api.services.garden.domain.models.GrowingTip.CELERY_FLAVOR -> GrpcGrowingTip.CELERY_FLAVOR
    com.tometrics.api.services.garden.domain.models.GrowingTip.SIMILAR_TO_ARTICHOKE -> GrpcGrowingTip.SIMILAR_TO_ARTICHOKE
    com.tometrics.api.services.garden.domain.models.GrowingTip.INVASIVE_TENDENCIES -> GrpcGrowingTip.INVASIVE_TENDENCIES
    com.tometrics.api.services.garden.domain.models.GrowingTip.PEST_RESISTANT -> GrpcGrowingTip.PEST_RESISTANT
    com.tometrics.api.services.garden.domain.models.GrowingTip.COLORFUL_TUBERS -> GrpcGrowingTip.COLORFUL_TUBERS
    com.tometrics.api.services.garden.domain.models.GrowingTip.PLANT_WHOLE_FRUIT -> GrpcGrowingTip.PLANT_WHOLE_FRUIT
    com.tometrics.api.services.garden.domain.models.GrowingTip.PERENNIAL_IN_TROPICS -> GrpcGrowingTip.PERENNIAL_IN_TROPICS
    com.tometrics.api.services.garden.domain.models.GrowingTip.MEDICINAL_PROPERTIES -> GrpcGrowingTip.MEDICINAL_PROPERTIES
    com.tometrics.api.services.garden.domain.models.GrowingTip.EDIBLE_WHEN_YOUNG -> GrpcGrowingTip.EDIBLE_WHEN_YOUNG
    com.tometrics.api.services.garden.domain.models.GrowingTip.DRY_FOR_SPONGES -> GrpcGrowingTip.DRY_FOR_SPONGES
    com.tometrics.api.services.garden.domain.models.GrowingTip.ALL_PARTS_EDIBLE -> GrpcGrowingTip.ALL_PARTS_EDIBLE
    com.tometrics.api.services.garden.domain.models.GrowingTip.NUTRITIOUS_LEAVES -> GrpcGrowingTip.NUTRITIOUS_LEAVES
    com.tometrics.api.services.garden.domain.models.GrowingTip.TREE_VEGETABLE -> GrpcGrowingTip.TREE_VEGETABLE
    com.tometrics.api.services.garden.domain.models.GrowingTip.PLANT_MULTIPLES -> GrpcGrowingTip.PLANT_MULTIPLES
    com.tometrics.api.services.garden.domain.models.GrowingTip.HARVEST_WHEN_HUSKS_SPLIT -> GrpcGrowingTip.HARVEST_WHEN_HUSKS_SPLIT
    com.tometrics.api.services.garden.domain.models.GrowingTip.REMOVE_HUSKS_BEFORE_USE -> GrpcGrowingTip.REMOVE_HUSKS_BEFORE_USE
    com.tometrics.api.services.garden.domain.models.GrowingTip.SAPONIN_REMOVAL -> GrpcGrowingTip.SAPONIN_REMOVAL
    com.tometrics.api.services.garden.domain.models.GrowingTip.EDIBLE_LEAVES -> GrpcGrowingTip.EDIBLE_LEAVES
    com.tometrics.api.services.garden.domain.models.GrowingTip.GRAIN_AND_LEAF_CROP -> GrpcGrowingTip.GRAIN_AND_LEAF_CROP
    com.tometrics.api.services.garden.domain.models.GrowingTip.QUICK_GROWING -> GrpcGrowingTip.QUICK_GROWING
    com.tometrics.api.services.garden.domain.models.GrowingTip.EXCELLENT_COVER_CROP -> GrpcGrowingTip.EXCELLENT_COVER_CROP
    com.tometrics.api.services.garden.domain.models.GrowingTip.ATTRACTS_POLLINATORS -> GrpcGrowingTip.ATTRACTS_POLLINATORS
    com.tometrics.api.services.garden.domain.models.GrowingTip.IMPROVES_SOIL -> GrpcGrowingTip.IMPROVES_SOIL
    com.tometrics.api.services.garden.domain.models.GrowingTip.SEED_CLEANING_REQUIRED -> GrpcGrowingTip.SEED_CLEANING_REQUIRED
    com.tometrics.api.services.garden.domain.models.GrowingTip.BIRD_PROTECTION_NEEDED -> GrpcGrowingTip.BIRD_PROTECTION_NEEDED
    com.tometrics.api.services.garden.domain.models.GrowingTip.OIL_AND_FIBER_CROP -> GrpcGrowingTip.OIL_AND_FIBER_CROP
    com.tometrics.api.services.garden.domain.models.GrowingTip.HARVEST_FOR_SEEDS -> GrpcGrowingTip.HARVEST_FOR_SEEDS
    com.tometrics.api.services.garden.domain.models.GrowingTip.FORCED_FOR_CHICONS -> GrpcGrowingTip.FORCED_FOR_CHICONS
    com.tometrics.api.services.garden.domain.models.GrowingTip.COFFEE_SUBSTITUTE -> GrpcGrowingTip.COFFEE_SUBSTITUTE
    com.tometrics.api.services.garden.domain.models.GrowingTip.BITTER_FLAVOR -> GrpcGrowingTip.BITTER_FLAVOR
    com.tometrics.api.services.garden.domain.models.GrowingTip.MULTIPLE_ROOTS -> GrpcGrowingTip.MULTIPLE_ROOTS
    com.tometrics.api.services.garden.domain.models.GrowingTip.SWEET_FLAVOR -> GrpcGrowingTip.SWEET_FLAVOR
    com.tometrics.api.services.garden.domain.models.GrowingTip.MULTI_USE_PLANT -> GrpcGrowingTip.MULTI_USE_PLANT
    com.tometrics.api.services.garden.domain.models.GrowingTip.SPINACH_SUBSTITUTE -> GrpcGrowingTip.SPINACH_SUBSTITUTE
    com.tometrics.api.services.garden.domain.models.GrowingTip.SLOW_TO_ESTABLISH -> GrpcGrowingTip.SLOW_TO_ESTABLISH
    com.tometrics.api.services.garden.domain.models.GrowingTip.COASTAL_NATIVE -> GrpcGrowingTip.COASTAL_NATIVE
    com.tometrics.api.services.garden.domain.models.GrowingTip.SALT_TOLERANT -> GrpcGrowingTip.SALT_TOLERANT
    com.tometrics.api.services.garden.domain.models.GrowingTip.HARVEST_STEMS_AND_LEAVES -> GrpcGrowingTip.HARVEST_STEMS_AND_LEAVES
    com.tometrics.api.services.garden.domain.models.GrowingTip.HEAT_TOLERANT_BRASSICA -> GrpcGrowingTip.HEAT_TOLERANT_BRASSICA
    com.tometrics.api.services.garden.domain.models.GrowingTip.EDIBLE_FLOWERS -> GrpcGrowingTip.EDIBLE_FLOWERS
    com.tometrics.api.services.garden.domain.models.GrowingTip.PEST_DETERRENT -> GrpcGrowingTip.PEST_DETERRENT
    com.tometrics.api.services.garden.domain.models.GrowingTip.COLORFUL_LEAVES -> GrpcGrowingTip.COLORFUL_LEAVES
    com.tometrics.api.services.garden.domain.models.GrowingTip.SELF_SEEDING -> GrpcGrowingTip.SELF_SEEDING
    com.tometrics.api.services.garden.domain.models.GrowingTip.HUMIDITY_TOLERANT -> GrpcGrowingTip.HUMIDITY_TOLERANT
    com.tometrics.api.services.garden.domain.models.GrowingTip.MUCILAGINOUS_TEXTURE -> GrpcGrowingTip.MUCILAGINOUS_TEXTURE
    com.tometrics.api.services.garden.domain.models.GrowingTip.FAST_GROWING -> GrpcGrowingTip.FAST_GROWING
    com.tometrics.api.services.garden.domain.models.GrowingTip.SHORT_DAY_TUBER_FORMATION -> GrpcGrowingTip.SHORT_DAY_TUBER_FORMATION
    com.tometrics.api.services.garden.domain.models.GrowingTip.BIRD_PROTECTION -> GrpcGrowingTip.BIRD_PROTECTION
    com.tometrics.api.services.garden.domain.models.GrowingTip.INOCULANT_BENEFICIAL -> GrpcGrowingTip.INOCULANT_BENEFICIAL
    GrowingTip.DROUGHT_TOLERANT -> GrpcGrowingTip.DROUGHT_TOLERANT
}
