package com.tometrics.api.services.commonclient.models.garden

import com.tometrics.api.services.protos.WaterRequirement

enum class GrpcWaterRequirement {
    LOW,
    MEDIUM,
    HIGH;

    fun toNetwork(): WaterRequirement = when (this) {
        LOW -> WaterRequirement.WATER_LOW
        MEDIUM -> WaterRequirement.WATER_MEDIUM
        HIGH -> WaterRequirement.WATER_HIGH
    }

    companion object {
        fun fromNetwork(network: WaterRequirement): GrpcWaterRequirement = when (network) {
            WaterRequirement.WATER_LOW -> LOW
            WaterRequirement.WATER_MEDIUM -> MEDIUM
            WaterRequirement.WATER_HIGH -> HIGH
            WaterRequirement.UNRECOGNIZED -> throw IllegalArgumentException("Unknown WaterRequirement: $network")
        }
    }
}