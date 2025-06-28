package com.tometrics.api.services.commonclient.models.garden

import com.tometrics.api.services.protos.SunlightRequirement

enum class GrpcSunlightRequirement {
    FULL_SUN,
    PARTIAL_SUN,
    PARTIAL_SHADE,
    FULL_SHADE;

    fun toNetwork(): SunlightRequirement = when (this) {
        FULL_SUN -> SunlightRequirement.FULL_SUN
        PARTIAL_SUN -> SunlightRequirement.PARTIAL_SUN
        PARTIAL_SHADE -> SunlightRequirement.PARTIAL_SHADE
        FULL_SHADE -> SunlightRequirement.FULL_SHADE
    }

    companion object {
        fun fromNetwork(network: SunlightRequirement): GrpcSunlightRequirement = when (network) {
            SunlightRequirement.FULL_SUN -> FULL_SUN
            SunlightRequirement.PARTIAL_SUN -> PARTIAL_SUN
            SunlightRequirement.PARTIAL_SHADE -> PARTIAL_SHADE
            SunlightRequirement.FULL_SHADE -> FULL_SHADE
            SunlightRequirement.UNRECOGNIZED -> throw IllegalArgumentException("Unknown SunlightRequirement: $network")
        }
    }
}