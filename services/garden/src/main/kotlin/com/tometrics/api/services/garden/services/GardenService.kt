package com.tometrics.api.services.garden.services

import com.tometrics.api.auth.domain.models.Requester
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.commongrpc.models.garden.*
import com.tometrics.api.services.commongrpc.services.GardenGrpcService
import com.tometrics.api.services.garden.db.GardenDao
import com.tometrics.api.services.garden.db.models.toDomain
import com.tometrics.api.services.garden.domain.models.*
import io.ktor.server.plugins.*
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

interface GardenService : GardenGrpcService {
    suspend fun getAll(requester: Requester): List<Planting>
    suspend fun getById(requester: Requester, id: PlantingId): Planting
    suspend fun delete(requester: Requester, id: PlantingId)
    suspend fun update(
        requester: Requester,
        id: PlantingId,
        newQuantity: Int?,
        newName: String? = null,
        newDiary: String? = null,
        newHarvested: Boolean? = null,
    ): Planting
    suspend fun add(requester: Requester, plantId: PlantId, quantity: Int): Planting
}

class DefaultGardenService(
    private val gardenDao: GardenDao,
    private val plantService: PlantService,
) : GardenService {

    override suspend fun getAll(requester: Requester): List<Planting> {
        return gardenDao.getAll(requester.userId).map { planting ->
            val plant = plantService.getById(planting.plantId)
            planting.toDomain(plant)
        }
    }

    override suspend fun getById(requester: Requester, id: PlantingId): Planting {
        val planting = gardenDao.find(id) ?: throw NotFoundException("Planting not found")
        if (planting.userId != requester.userId) throw BadRequestException("Planting doesn't belong to this user")
        val plant = plantService.getById(planting.plantId)

        return planting.toDomain(plant)
    }

    override suspend fun delete(requester: Requester, id: PlantingId) {
        val planting = gardenDao.find(id) ?: throw NotFoundException("Planting not found")
        if (planting.userId != requester.userId) throw BadRequestException("Planting doesn't belong to this user")
        gardenDao.delete(id)
    }

    override suspend fun update(requester: Requester, id: PlantingId, newQuantity: Int?, newName: String?, newDiary: String?, newHarvested: Boolean?): Planting {
        val planting = gardenDao.find(id) ?: throw NotFoundException("Planting not found")
        if (planting.userId != requester.userId) throw BadRequestException("Planting doesn't belong to this user")
        gardenDao.update(id, newQuantity, newName, newDiary, newHarvested)

        return getById(requester, id)
    }

    override suspend fun add(requester: Requester, plantId: PlantId, quantity: Int): Planting {
        val plant = plantService.getById(plantId)
        val samePlantPlantings = gardenDao.getSamePlantPlantings(
            requester.userId,
            plantId,
        )
        val plantingName = if (samePlantPlantings.isEmpty()) {
            plant.name
        } else {
            val dateStr = DateTimeFormatter
                .ofPattern("yyyy-MM-dd")
                .withZone(ZoneId.systemDefault())
                .format(Instant.now())
            val count = samePlantPlantings.count {
                it.createdAt.truncatedTo(ChronoUnit.DAYS) == Instant.now().truncatedTo(ChronoUnit.DAYS)
            }

            if (count == 0) {
                "${plant.name} $dateStr"
            } else {
                "${plant.name} $dateStr #${count + 1}"
            }
        }
        val id: PlantingId = gardenDao.insert(
            userId = requester.userId,
            plantId = plantId,
            name = plantingName,
            quantity = quantity,
            readyToHarvestAt = Instant.now().plus(plant.timeToHarvest.toLong(), ChronoUnit.DAYS),
            diary = "",
            harvested = false,
        )
        return getById(requester, id)
    }

    override suspend fun getAllReadyForHarvestToday(): Map<UserId, List<GrpcPlanting>> {
        val plantings = gardenDao.getAllReadyForHarvestToday()
        val plantIds = plantings.map { it.plantId }.toSet()
        val plantsMap = plantService.getAllByIds(plantIds).associateBy { it.id }
        return plantings
            .groupBy { it.userId }
            .mapValues { (_, plantings) ->
                plantings.mapNotNull { planting ->
                    val plant = plantsMap[planting.plantId] ?: return@mapNotNull null
                    val domain = planting.toDomain(plant)
                    domain.toGrpc()
                }
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

private fun YieldUnit.toGrpc() = when (this) {
    YieldUnit.UNIT -> GrpcYieldUnit.UNIT
    YieldUnit.KG -> GrpcYieldUnit.KG
    YieldUnit.GRAMS -> GrpcYieldUnit.GRAMS
    YieldUnit.LB -> GrpcYieldUnit.LB
    YieldUnit.OZ -> GrpcYieldUnit.OZ
}

private fun ClimateZones.toGrpc() = GrpcClimateZones(
    temperate = temperate,
    mediterranean = mediterranean,
    continental = continental,
    tropical = tropical,
    arid = arid
)

private fun SpacingRequirement.toGrpc() = when (this) {
    SpacingRequirement.VERY_CLOSE -> GrpcSpacingRequirement.VERY_CLOSE
    SpacingRequirement.CLOSE -> GrpcSpacingRequirement.CLOSE
    SpacingRequirement.MODERATE -> GrpcSpacingRequirement.MODERATE
    SpacingRequirement.WIDE -> GrpcSpacingRequirement.WIDE
    SpacingRequirement.VERY_WIDE -> GrpcSpacingRequirement.VERY_WIDE
}

private fun SunlightRequirement.toGrpc() = when (this) {
    SunlightRequirement.FULL_SUN -> GrpcSunlightRequirement.FULL_SUN
    SunlightRequirement.PARTIAL_SUN -> GrpcSunlightRequirement.PARTIAL_SUN
    SunlightRequirement.PARTIAL_SHADE -> GrpcSunlightRequirement.PARTIAL_SHADE
    SunlightRequirement.FULL_SHADE -> GrpcSunlightRequirement.FULL_SHADE
}

private fun DailySunlightRequirement.toGrpc() = when (this) {
    DailySunlightRequirement.LOW -> GrpcDailySunlightRequirement.LOW
    DailySunlightRequirement.MEDIUM -> GrpcDailySunlightRequirement.MEDIUM
    DailySunlightRequirement.HIGH -> GrpcDailySunlightRequirement.HIGH
}

private fun SoilType.toGrpc() = when (this) {
    SoilType.SANDY -> GrpcSoilType.SANDY
    SoilType.LOAMY -> GrpcSoilType.LOAMY
    SoilType.CLAY -> GrpcSoilType.CLAY
    SoilType.PEATY -> GrpcSoilType.PEATY
    SoilType.CHALKY -> GrpcSoilType.CHALKY
    SoilType.SILTY -> GrpcSoilType.SILTY
}

private fun WaterRequirement.toGrpc() = when (this) {
    WaterRequirement.LOW -> GrpcWaterRequirement.LOW
    WaterRequirement.MEDIUM -> GrpcWaterRequirement.MEDIUM
    WaterRequirement.HIGH -> GrpcWaterRequirement.HIGH
}

private fun GrowthHabit.toGrpc() = when (this) {
    GrowthHabit.DETERMINATE -> GrpcGrowthHabit.DETERMINATE
    GrowthHabit.INDETERMINATE -> GrpcGrowthHabit.INDETERMINATE
    GrowthHabit.BUSH -> GrpcGrowthHabit.BUSH
    GrowthHabit.VINING -> GrpcGrowthHabit.VINING
    GrowthHabit.UPRIGHT -> GrpcGrowthHabit.UPRIGHT
    GrowthHabit.ROSETTE -> GrpcGrowthHabit.ROSETTE
    GrowthHabit.CLUMPING -> GrpcGrowthHabit.CLUMPING
    GrowthHabit.SPREADING -> GrpcGrowthHabit.SPREADING
    GrowthHabit.MOUNDING -> GrpcGrowthHabit.MOUNDING
}

private fun GrowingTip.toGrpc() = when (this) {
    GrowingTip.PRUNE_SUCKERS -> GrpcGrowingTip.PRUNE_SUCKERS
    GrowingTip.PROVIDE_SUPPORT -> GrpcGrowingTip.PROVIDE_SUPPORT
    GrowingTip.MULCH_SOIL -> GrpcGrowingTip.MULCH_SOIL
    GrowingTip.WATER_AT_BASE -> GrpcGrowingTip.WATER_AT_BASE
    GrowingTip.CROP_ROTATION -> GrpcGrowingTip.CROP_ROTATION
    GrowingTip.HILL_SOIL -> GrpcGrowingTip.HILL_SOIL
    GrowingTip.PLANT_DEEP -> GrpcGrowingTip.PLANT_DEEP
    GrowingTip.AVOID_LIGHT_EXPOSURE -> GrpcGrowingTip.AVOID_LIGHT_EXPOSURE
    GrowingTip.HARVEST_AFTER_FLOWERING -> GrpcGrowingTip.HARVEST_AFTER_FLOWERING
    GrowingTip.COOL_STORAGE -> GrpcGrowingTip.COOL_STORAGE
    GrowingTip.LOOSEN_SOIL -> GrpcGrowingTip.LOOSEN_SOIL
    GrowingTip.THIN_SEEDLINGS -> GrpcGrowingTip.THIN_SEEDLINGS
    GrowingTip.CONSISTENT_MOISTURE -> GrpcGrowingTip.CONSISTENT_MOISTURE
    GrowingTip.AVOID_ROCKY_SOIL -> GrpcGrowingTip.AVOID_ROCKY_SOIL
    GrowingTip.PREVENT_GREENING -> GrpcGrowingTip.PREVENT_GREENING
    GrowingTip.PLANT_TIPS_UP -> GrpcGrowingTip.PLANT_TIPS_UP
    GrowingTip.REDUCE_WATERING_AT_MATURITY -> GrpcGrowingTip.REDUCE_WATERING_AT_MATURITY
    GrowingTip.CURE_AFTER_HARVEST -> GrpcGrowingTip.CURE_AFTER_HARVEST
    GrowingTip.DAY_LENGTH_SENSITIVE -> GrpcGrowingTip.DAY_LENGTH_SENSITIVE
    GrowingTip.SUPPORT_WHEN_FRUITING -> GrpcGrowingTip.SUPPORT_WHEN_FRUITING
    GrowingTip.HARVEST_WHEN_FIRM -> GrpcGrowingTip.HARVEST_WHEN_FIRM
    GrowingTip.PREVENT_BLOSSOM_END_ROT -> GrpcGrowingTip.PREVENT_BLOSSOM_END_ROT
    GrowingTip.PRODUCES_UNTIL_FROST -> GrpcGrowingTip.PRODUCES_UNTIL_FROST
    GrowingTip.SUCCESSION_PLANTING -> GrpcGrowingTip.SUCCESSION_PLANTING
    GrowingTip.DIRECT_SOW -> GrpcGrowingTip.DIRECT_SOW
    GrowingTip.COLD_HARDY -> GrpcGrowingTip.COLD_HARDY
    GrowingTip.HEAT_SENSITIVE -> GrpcGrowingTip.HEAT_SENSITIVE
    GrowingTip.HEAT_TOLERANT -> GrpcGrowingTip.HEAT_TOLERANT
    GrowingTip.PINCH_FLOWERS -> GrpcGrowingTip.PINCH_FLOWERS
    GrowingTip.DEADHEAD -> GrpcGrowingTip.DEADHEAD
    GrowingTip.REQUIRES_POLLINATION -> GrpcGrowingTip.REQUIRES_POLLINATION
    GrowingTip.HAND_POLLINATE -> GrpcGrowingTip.HAND_POLLINATE
    GrowingTip.AVOID_OVERHEAD_WATERING -> GrpcGrowingTip.AVOID_OVERHEAD_WATERING
    GrowingTip.PLANT_IN_BLOCKS -> GrpcGrowingTip.PLANT_IN_BLOCKS
    GrowingTip.HARVEST_REGULARLY -> GrpcGrowingTip.HARVEST_REGULARLY
    GrowingTip.BLANCHING -> GrpcGrowingTip.BLANCHING
    GrowingTip.ACIDIC_SOIL -> GrpcGrowingTip.ACIDIC_SOIL
    GrowingTip.ALKALINE_SOIL -> GrpcGrowingTip.ALKALINE_SOIL
    GrowingTip.DEEP_ROOT_SYSTEM -> GrpcGrowingTip.DEEP_ROOT_SYSTEM
    GrowingTip.SHALLOW_ROOT_SYSTEM -> GrpcGrowingTip.SHALLOW_ROOT_SYSTEM
    GrowingTip.MOUND_PLANTING -> GrpcGrowingTip.MOUND_PLANTING
    GrowingTip.TRENCH_PLANTING -> GrpcGrowingTip.TRENCH_PLANTING
    GrowingTip.REQUIRES_STAKING -> GrpcGrowingTip.REQUIRES_STAKING
    GrowingTip.NITROGEN_FIXING -> GrpcGrowingTip.NITROGEN_FIXING
    GrowingTip.CUT_AND_COME_AGAIN -> GrpcGrowingTip.CUT_AND_COME_AGAIN
    GrowingTip.PERENNIAL_CROP -> GrpcGrowingTip.PERENNIAL_CROP
    GrowingTip.HARVEST_SELECTIVELY -> GrpcGrowingTip.HARVEST_SELECTIVELY
    GrowingTip.DROUGHT_RESISTANT -> GrpcGrowingTip.DROUGHT_RESISTANT
    GrowingTip.AVOID_TRANSPLANTING -> GrpcGrowingTip.AVOID_TRANSPLANTING
    GrowingTip.COMPANION_BENEFICIAL -> GrpcGrowingTip.COMPANION_BENEFICIAL
    GrowingTip.SHADE_TOLERANT -> GrpcGrowingTip.SHADE_TOLERANT
    GrowingTip.EARLY_SPRING_CROP -> GrpcGrowingTip.EARLY_SPRING_CROP
    GrowingTip.FALL_CROP -> GrpcGrowingTip.FALL_CROP
    GrowingTip.OVERWINTER -> GrpcGrowingTip.OVERWINTER
    GrowingTip.COOL_WEATHER_CROP -> GrpcGrowingTip.COOL_WEATHER_CROP
    GrowingTip.REMOVE_LOWER_LEAVES -> GrpcGrowingTip.REMOVE_LOWER_LEAVES
    GrowingTip.FERTILE_SOIL -> GrpcGrowingTip.FERTILE_SOIL
    GrowingTip.NEEDS_SUPPORT_CAGE -> GrpcGrowingTip.NEEDS_SUPPORT_CAGE
    GrowingTip.FLOATING_ROW_COVER -> GrpcGrowingTip.FLOATING_ROW_COVER
    GrowingTip.SLOW_TO_GERMINATE -> GrpcGrowingTip.SLOW_TO_GERMINATE
    GrowingTip.SOAK_SEEDS -> GrpcGrowingTip.SOAK_SEEDS
    GrowingTip.PINCH_GROWING_TIPS -> GrpcGrowingTip.PINCH_GROWING_TIPS
    GrowingTip.STRATIFICATION -> GrpcGrowingTip.STRATIFICATION
    GrowingTip.START_INDOORS -> GrpcGrowingTip.START_INDOORS
    GrowingTip.LOOSE_SOIL -> GrpcGrowingTip.LOOSE_SOIL
    GrowingTip.HARVEST_WHEN_SMALL -> GrpcGrowingTip.HARVEST_WHEN_SMALL
    GrowingTip.MINIMAL_COMPANIONS -> GrpcGrowingTip.MINIMAL_COMPANIONS
    GrowingTip.FROST_IMPROVES_FLAVOR -> GrpcGrowingTip.FROST_IMPROVES_FLAVOR
    GrowingTip.AVOID_HARVESTING_FIRST_YEAR -> GrpcGrowingTip.AVOID_HARVESTING_FIRST_YEAR
    GrowingTip.REMOVE_FLOWER_STALKS -> GrpcGrowingTip.REMOVE_FLOWER_STALKS
    GrowingTip.DIVIDE_EVERY_FEW_YEARS -> GrpcGrowingTip.DIVIDE_EVERY_FEW_YEARS
    GrowingTip.DIVIDE_BULBS -> GrpcGrowingTip.DIVIDE_BULBS
    GrowingTip.NEEDS_CONSTANT_MOISTURE -> GrpcGrowingTip.NEEDS_CONSTANT_MOISTURE
    GrowingTip.AQUATIC_FRIENDLY -> GrpcGrowingTip.AQUATIC_FRIENDLY
    GrowingTip.HARVEST_AFTER_FROST -> GrpcGrowingTip.HARVEST_AFTER_FROST
    GrowingTip.CONTAIN_SPREAD -> GrpcGrowingTip.CONTAIN_SPREAD
    GrowingTip.NEEDS_SPACE -> GrpcGrowingTip.NEEDS_SPACE
    GrowingTip.FULL_MATURITY_FOR_STORAGE -> GrpcGrowingTip.FULL_MATURITY_FOR_STORAGE
    GrowingTip.HARVEST_IN_FALL_OR_SPRING -> GrpcGrowingTip.HARVEST_IN_FALL_OR_SPRING
    GrowingTip.NEEDS_LONG_SEASON -> GrpcGrowingTip.NEEDS_LONG_SEASON
    GrowingTip.PROPER_PROCESSING_REQUIRED -> GrpcGrowingTip.PROPER_PROCESSING_REQUIRED
    GrowingTip.SUBMERGED_PLANTING -> GrpcGrowingTip.SUBMERGED_PLANTING
    GrowingTip.SPECIAL_GROWING_CONDITIONS -> GrpcGrowingTip.SPECIAL_GROWING_CONDITIONS
    GrowingTip.SLOW_GROWING -> GrpcGrowingTip.SLOW_GROWING
    GrowingTip.WAIT_FOR_WARM_SOIL -> GrpcGrowingTip.WAIT_FOR_WARM_SOIL
    GrowingTip.AVOID_RICH_SOIL -> GrpcGrowingTip.AVOID_RICH_SOIL
    GrowingTip.SHALLOW_PLANTING -> GrpcGrowingTip.SHALLOW_PLANTING
    GrowingTip.EDIBLE_ROOTS_ONLY -> GrpcGrowingTip.EDIBLE_ROOTS_ONLY
    GrowingTip.LONG_GROWING_SEASON -> GrpcGrowingTip.LONG_GROWING_SEASON
    GrowingTip.PREVENT_FLOWERING -> GrpcGrowingTip.PREVENT_FLOWERING
    GrowingTip.ACIDIC_FLAVOR -> GrpcGrowingTip.ACIDIC_FLAVOR
    GrowingTip.CELERY_FLAVOR -> GrpcGrowingTip.CELERY_FLAVOR
    GrowingTip.SIMILAR_TO_ARTICHOKE -> GrpcGrowingTip.SIMILAR_TO_ARTICHOKE
    GrowingTip.INVASIVE_TENDENCIES -> GrpcGrowingTip.INVASIVE_TENDENCIES
    GrowingTip.PEST_RESISTANT -> GrpcGrowingTip.PEST_RESISTANT
    GrowingTip.COLORFUL_TUBERS -> GrpcGrowingTip.COLORFUL_TUBERS
    GrowingTip.PLANT_WHOLE_FRUIT -> GrpcGrowingTip.PLANT_WHOLE_FRUIT
    GrowingTip.PERENNIAL_IN_TROPICS -> GrpcGrowingTip.PERENNIAL_IN_TROPICS
    GrowingTip.MEDICINAL_PROPERTIES -> GrpcGrowingTip.MEDICINAL_PROPERTIES
    GrowingTip.EDIBLE_WHEN_YOUNG -> GrpcGrowingTip.EDIBLE_WHEN_YOUNG
    GrowingTip.DRY_FOR_SPONGES -> GrpcGrowingTip.DRY_FOR_SPONGES
    GrowingTip.ALL_PARTS_EDIBLE -> GrpcGrowingTip.ALL_PARTS_EDIBLE
    GrowingTip.NUTRITIOUS_LEAVES -> GrpcGrowingTip.NUTRITIOUS_LEAVES
    GrowingTip.TREE_VEGETABLE -> GrpcGrowingTip.TREE_VEGETABLE
    GrowingTip.PLANT_MULTIPLES -> GrpcGrowingTip.PLANT_MULTIPLES
    GrowingTip.HARVEST_WHEN_HUSKS_SPLIT -> GrpcGrowingTip.HARVEST_WHEN_HUSKS_SPLIT
    GrowingTip.REMOVE_HUSKS_BEFORE_USE -> GrpcGrowingTip.REMOVE_HUSKS_BEFORE_USE
    GrowingTip.SAPONIN_REMOVAL -> GrpcGrowingTip.SAPONIN_REMOVAL
    GrowingTip.EDIBLE_LEAVES -> GrpcGrowingTip.EDIBLE_LEAVES
    GrowingTip.GRAIN_AND_LEAF_CROP -> GrpcGrowingTip.GRAIN_AND_LEAF_CROP
    GrowingTip.QUICK_GROWING -> GrpcGrowingTip.QUICK_GROWING
    GrowingTip.EXCELLENT_COVER_CROP -> GrpcGrowingTip.EXCELLENT_COVER_CROP
    GrowingTip.ATTRACTS_POLLINATORS -> GrpcGrowingTip.ATTRACTS_POLLINATORS
    GrowingTip.IMPROVES_SOIL -> GrpcGrowingTip.IMPROVES_SOIL
    GrowingTip.SEED_CLEANING_REQUIRED -> GrpcGrowingTip.SEED_CLEANING_REQUIRED
    GrowingTip.BIRD_PROTECTION_NEEDED -> GrpcGrowingTip.BIRD_PROTECTION_NEEDED
    GrowingTip.OIL_AND_FIBER_CROP -> GrpcGrowingTip.OIL_AND_FIBER_CROP
    GrowingTip.HARVEST_FOR_SEEDS -> GrpcGrowingTip.HARVEST_FOR_SEEDS
    GrowingTip.FORCED_FOR_CHICONS -> GrpcGrowingTip.FORCED_FOR_CHICONS
    GrowingTip.COFFEE_SUBSTITUTE -> GrpcGrowingTip.COFFEE_SUBSTITUTE
    GrowingTip.BITTER_FLAVOR -> GrpcGrowingTip.BITTER_FLAVOR
    GrowingTip.MULTIPLE_ROOTS -> GrpcGrowingTip.MULTIPLE_ROOTS
    GrowingTip.SWEET_FLAVOR -> GrpcGrowingTip.SWEET_FLAVOR
    GrowingTip.MULTI_USE_PLANT -> GrpcGrowingTip.MULTI_USE_PLANT
    GrowingTip.SPINACH_SUBSTITUTE -> GrpcGrowingTip.SPINACH_SUBSTITUTE
    GrowingTip.SLOW_TO_ESTABLISH -> GrpcGrowingTip.SLOW_TO_ESTABLISH
    GrowingTip.COASTAL_NATIVE -> GrpcGrowingTip.COASTAL_NATIVE
    GrowingTip.SALT_TOLERANT -> GrpcGrowingTip.SALT_TOLERANT
    GrowingTip.HARVEST_STEMS_AND_LEAVES -> GrpcGrowingTip.HARVEST_STEMS_AND_LEAVES
    GrowingTip.HEAT_TOLERANT_BRASSICA -> GrpcGrowingTip.HEAT_TOLERANT_BRASSICA
    GrowingTip.EDIBLE_FLOWERS -> GrpcGrowingTip.EDIBLE_FLOWERS
    GrowingTip.PEST_DETERRENT -> GrpcGrowingTip.PEST_DETERRENT
    GrowingTip.COLORFUL_LEAVES -> GrpcGrowingTip.COLORFUL_LEAVES
    GrowingTip.SELF_SEEDING -> GrpcGrowingTip.SELF_SEEDING
    GrowingTip.HUMIDITY_TOLERANT -> GrpcGrowingTip.HUMIDITY_TOLERANT
    GrowingTip.MUCILAGINOUS_TEXTURE -> GrpcGrowingTip.MUCILAGINOUS_TEXTURE
    GrowingTip.FAST_GROWING -> GrpcGrowingTip.FAST_GROWING
    GrowingTip.SHORT_DAY_TUBER_FORMATION -> GrpcGrowingTip.SHORT_DAY_TUBER_FORMATION
    GrowingTip.BIRD_PROTECTION -> GrpcGrowingTip.BIRD_PROTECTION
    GrowingTip.INOCULANT_BENEFICIAL -> GrpcGrowingTip.INOCULANT_BENEFICIAL
    GrowingTip.DROUGHT_TOLERANT -> GrpcGrowingTip.DROUGHT_TOLERANT
}
