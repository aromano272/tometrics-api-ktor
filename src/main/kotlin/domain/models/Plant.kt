package com.tometrics.api.domain.models

import kotlinx.serialization.Serializable

enum class SpacingRequirement {
    VERY_CLOSE,    // 5-15 cm
    CLOSE,         // 15-30 cm
    MODERATE,      // 30-60 cm
    WIDE,          // 60-90 cm
    VERY_WIDE      // 90+ cm
}

enum class SunlightRequirement {
    FULL_SUN,
    PARTIAL_SUN,
    PARTIAL_SHADE,
    FULL_SHADE
}

enum class DailySunlightRequirement {
    LOW,    // Less than 4 hours
    MEDIUM, // 4-6 hours
    HIGH    // 6+ hours
}

enum class SoilType {
    SANDY,
    LOAMY,
    CLAY,
    PEATY,
    CHALKY,
    SILTY
}

enum class WaterRequirement {
    LOW,    // Drought-tolerant, infrequent watering
    MEDIUM, // Regular watering, moist but not wet
    HIGH    // Consistently moist, frequent watering
}

enum class GrowthHabit {
    DETERMINATE,    // Bush-type that grows to a certain height and stops
    INDETERMINATE,  // Vining type that continues growing
    BUSH,          // Compact growing habit
    VINING,        // Climbing or sprawling habit
    UPRIGHT,       // Grows straight up with minimal spread
    ROSETTE,       // Grows in a circular pattern from center
    CLUMPING,      // Forms clumps or clusters
    SPREADING,     // Spreads horizontally
    MOUNDING       // Forms a mound shape
}

enum class GrowingTip {
    PRUNE_SUCKERS,
    PROVIDE_SUPPORT,
    MULCH_SOIL,
    WATER_AT_BASE,
    CROP_ROTATION,
    HILL_SOIL,
    PLANT_DEEP,
    AVOID_LIGHT_EXPOSURE,
    HARVEST_AFTER_FLOWERING,
    COOL_STORAGE,
    LOOSEN_SOIL,
    THIN_SEEDLINGS,
    CONSISTENT_MOISTURE,
    AVOID_ROCKY_SOIL,
    PREVENT_GREENING,
    PLANT_TIPS_UP,
    REDUCE_WATERING_AT_MATURITY,
    CURE_AFTER_HARVEST,
    DAY_LENGTH_SENSITIVE,
    SUPPORT_WHEN_FRUITING,
    HARVEST_WHEN_FIRM,
    PREVENT_BLOSSOM_END_ROT,
    PRODUCES_UNTIL_FROST,
    SUCCESSION_PLANTING,
    DIRECT_SOW,
    COLD_HARDY,
    HEAT_SENSITIVE,
    HEAT_TOLERANT,
    PINCH_FLOWERS,
    DEADHEAD,
    REQUIRES_POLLINATION,
    HAND_POLLINATE,
    AVOID_OVERHEAD_WATERING,
    PLANT_IN_BLOCKS,
    HARVEST_REGULARLY,
    BLANCHING,
    ACIDIC_SOIL,
    ALKALINE_SOIL,
    DEEP_ROOT_SYSTEM,
    SHALLOW_ROOT_SYSTEM,
    MOUND_PLANTING,
    TRENCH_PLANTING,
    REQUIRES_STAKING,
    NITROGEN_FIXING,
    CUT_AND_COME_AGAIN,
    PERENNIAL_CROP,
    HARVEST_SELECTIVELY,
    DROUGHT_RESISTANT,
    AVOID_TRANSPLANTING,
    COMPANION_BENEFICIAL,
    SHADE_TOLERANT,
    EARLY_SPRING_CROP,
    FALL_CROP,
    OVERWINTER,
    COOL_WEATHER_CROP,
    REMOVE_LOWER_LEAVES,
    FERTILE_SOIL,
    NEEDS_SUPPORT_CAGE,
    FLOATING_ROW_COVER,
    SLOW_TO_GERMINATE,
    SOAK_SEEDS,
    PINCH_GROWING_TIPS,
    STRATIFICATION,
    START_INDOORS,
    LOOSE_SOIL,
    HARVEST_WHEN_SMALL,
    MINIMAL_COMPANIONS,
    FROST_IMPROVES_FLAVOR,
    AVOID_HARVESTING_FIRST_YEAR,
    REMOVE_FLOWER_STALKS,
    DIVIDE_EVERY_FEW_YEARS,
    DIVIDE_BULBS,
    NEEDS_CONSTANT_MOISTURE,
    AQUATIC_FRIENDLY,
    HARVEST_AFTER_FROST,
    CONTAIN_SPREAD,
    NEEDS_SPACE,
    FULL_MATURITY_FOR_STORAGE,
    HARVEST_IN_FALL_OR_SPRING,
    NEEDS_LONG_SEASON,
    PROPER_PROCESSING_REQUIRED,
    SUBMERGED_PLANTING,
    SPECIAL_GROWING_CONDITIONS,
    SLOW_GROWING,
    WAIT_FOR_WARM_SOIL,
    AVOID_RICH_SOIL,
    SHALLOW_PLANTING,
    EDIBLE_ROOTS_ONLY,
    LONG_GROWING_SEASON,
    PREVENT_FLOWERING,
    ACIDIC_FLAVOR,
    CELERY_FLAVOR,
    SIMILAR_TO_ARTICHOKE,
    INVASIVE_TENDENCIES,
    PEST_RESISTANT,
    COLORFUL_TUBERS,
    PLANT_WHOLE_FRUIT,
    PERENNIAL_IN_TROPICS,
    MEDICINAL_PROPERTIES,
    EDIBLE_WHEN_YOUNG,
    DRY_FOR_SPONGES,
    ALL_PARTS_EDIBLE,
    NUTRITIOUS_LEAVES,
    TREE_VEGETABLE,
    PLANT_MULTIPLES,
    HARVEST_WHEN_HUSKS_SPLIT,
    REMOVE_HUSKS_BEFORE_USE,
    SAPONIN_REMOVAL,
    EDIBLE_LEAVES,
    GRAIN_AND_LEAF_CROP,
    QUICK_GROWING,
    EXCELLENT_COVER_CROP,
    ATTRACTS_POLLINATORS,
    IMPROVES_SOIL,
    SEED_CLEANING_REQUIRED,
    BIRD_PROTECTION_NEEDED,
    OIL_AND_FIBER_CROP,
    HARVEST_FOR_SEEDS,
    FORCED_FOR_CHICONS,
    COFFEE_SUBSTITUTE,
    BITTER_FLAVOR,
    MULTIPLE_ROOTS,
    SWEET_FLAVOR,
    MULTI_USE_PLANT,
    SPINACH_SUBSTITUTE,
    SLOW_TO_ESTABLISH,
    COASTAL_NATIVE,
    SALT_TOLERANT,
    HARVEST_STEMS_AND_LEAVES,
    HEAT_TOLERANT_BRASSICA,
    EDIBLE_FLOWERS,
    PEST_DETERRENT,
    COLORFUL_LEAVES,
    SELF_SEEDING,
    HUMIDITY_TOLERANT,
    MUCILAGINOUS_TEXTURE,
    FAST_GROWING,
    SHORT_DAY_TUBER_FORMATION,
    BIRD_PROTECTION
}

@Serializable
data class ClimateZones(
    val temperate: List<Month>,
    val mediterranean: List<Month>,
    val continental: List<Month>,
    val tropical: List<Month>,
    val arid: List<Month>
)

enum class YieldUnit {
    UNIT, KG, GRAMS
}

@Serializable
data class PlantYield(
    val from: Float,
    val to: Float,
    val unit: YieldUnit,
)

typealias PlantId = Int

@Serializable
data class PlantRef(
    val id: PlantId,
    val name: String,
)

@Serializable
data class Plant(
    val id: PlantId,
    val name: String,
    val timeToHarvest: Int,
    val yieldPerPlant: PlantYield,
    val yieldPerSqM: PlantYield,
    val companionPlants: List<String>,
    val climateZones: ClimateZones,
    val spacing: SpacingRequirement,
    val sunlight: SunlightRequirement,
    val dailySunlight: DailySunlightRequirement,
    val soilType: List<SoilType>,
    val waterRequirement: WaterRequirement,
    val growthHabit: GrowthHabit,
    val growingTips: List<GrowingTip>
)
