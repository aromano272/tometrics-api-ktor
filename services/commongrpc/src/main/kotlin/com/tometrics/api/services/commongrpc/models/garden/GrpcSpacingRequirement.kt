package com.tometrics.api.services.commongrpc.models.garden

import com.tometrics.api.services.protos.SpacingRequirement

enum class GrpcSpacingRequirement {
    VERY_CLOSE,
    CLOSE,
    MODERATE,
    WIDE,
    VERY_WIDE;

    fun toNetwork(): SpacingRequirement = when (this) {
        VERY_CLOSE -> SpacingRequirement.VERY_CLOSE
        CLOSE -> SpacingRequirement.CLOSE
        MODERATE -> SpacingRequirement.MODERATE
        WIDE -> SpacingRequirement.WIDE
        VERY_WIDE -> SpacingRequirement.VERY_WIDE
    }

    companion object {
        fun fromNetwork(network: SpacingRequirement): GrpcSpacingRequirement = when (network) {
            SpacingRequirement.VERY_CLOSE -> VERY_CLOSE
            SpacingRequirement.CLOSE -> CLOSE
            SpacingRequirement.MODERATE -> MODERATE
            SpacingRequirement.WIDE -> WIDE
            SpacingRequirement.VERY_WIDE -> VERY_WIDE
            SpacingRequirement.UNRECOGNIZED -> throw IllegalArgumentException("Unknown SpacingRequirement: $network")
        }
    }
}