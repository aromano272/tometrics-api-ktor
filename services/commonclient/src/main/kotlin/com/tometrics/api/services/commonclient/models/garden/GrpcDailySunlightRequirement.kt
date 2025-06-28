package com.tometrics.api.services.commonclient.models.garden

import com.tometrics.api.services.protos.DailySunlightRequirement

enum class GrpcDailySunlightRequirement {
    LOW,
    MEDIUM,
    HIGH;

    fun toNetwork(): DailySunlightRequirement = when (this) {
        LOW -> DailySunlightRequirement.LOW
        MEDIUM -> DailySunlightRequirement.MEDIUM
        HIGH -> DailySunlightRequirement.HIGH
    }

    companion object {
        fun fromNetwork(network: DailySunlightRequirement): GrpcDailySunlightRequirement = when (network) {
            DailySunlightRequirement.LOW -> LOW
            DailySunlightRequirement.MEDIUM -> MEDIUM
            DailySunlightRequirement.HIGH -> HIGH
            DailySunlightRequirement.UNRECOGNIZED -> throw IllegalArgumentException("Unknown DailySunlightRequirement: $network")
        }
    }
}