package com.tometrics.api.services.commongrpc.models.garden

import com.tometrics.api.services.protos.Plant

data class GrpcPlant(
    val id: Int,
    val name: String,
    val timeToHarvest: Int,
    val yieldPerPlant: GrpcPlantYield,
    val yieldPerSqM: GrpcPlantYield,
    val companionPlants: List<String>,
    val climateZones: GrpcClimateZones,
    val spacing: GrpcSpacingRequirement,
    val sunlight: GrpcSunlightRequirement,
    val dailySunlight: GrpcDailySunlightRequirement,
    val soilType: List<GrpcSoilType>,
    val waterRequirement: GrpcWaterRequirement,
    val growthHabit: GrpcGrowthHabit,
    val growingTips: List<GrpcGrowingTip>
) {
    fun toNetwork(): Plant {
        val builder = Plant.newBuilder()
            .setId(id)
            .setName(name)
            .setTimeToHarvest(timeToHarvest)
            .setYieldPerPlant(yieldPerPlant.toNetwork())
            .setYieldPerSqM(yieldPerSqM.toNetwork())
            .addAllCompanionPlants(companionPlants)
            .setClimateZones(climateZones.toNetwork())
            .setSpacing(spacing.toNetwork())
            .setSunlight(sunlight.toNetwork())
            .setDailySunlight(dailySunlight.toNetwork())
            .setWaterRequirement(waterRequirement.toNetwork())
            .setGrowthHabit(growthHabit.toNetwork())
        
        soilType.forEach { builder.addSoilType(it.toNetwork()) }
        growingTips.forEach { builder.addGrowingTips(it.toNetwork()) }
        
        return builder.build()
    }

    companion object {
        fun fromNetwork(network: Plant): GrpcPlant = GrpcPlant(
            id = network.id,
            name = network.name,
            timeToHarvest = network.timeToHarvest,
            yieldPerPlant = GrpcPlantYield.fromNetwork(network.yieldPerPlant),
            yieldPerSqM = GrpcPlantYield.fromNetwork(network.yieldPerSqM),
            companionPlants = network.companionPlantsList,
            climateZones = GrpcClimateZones.fromNetwork(network.climateZones),
            spacing = GrpcSpacingRequirement.fromNetwork(network.spacing),
            sunlight = GrpcSunlightRequirement.fromNetwork(network.sunlight),
            dailySunlight = GrpcDailySunlightRequirement.fromNetwork(network.dailySunlight),
            soilType = network.soilTypeList.map { GrpcSoilType.fromNetwork(it) },
            waterRequirement = GrpcWaterRequirement.fromNetwork(network.waterRequirement),
            growthHabit = GrpcGrowthHabit.fromNetwork(network.growthHabit),
            growingTips = network.growingTipsList.map { GrpcGrowingTip.fromNetwork(it) }
        )
    }
}