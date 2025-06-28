package com.tometrics.api.services.commonclient.models.garden

import com.tometrics.api.services.protos.SoilType

enum class GrpcSoilType {
    SANDY,
    LOAMY,
    CLAY,
    PEATY,
    CHALKY,
    SILTY;

    fun toNetwork(): SoilType = when (this) {
        SANDY -> SoilType.SANDY
        LOAMY -> SoilType.LOAMY
        CLAY -> SoilType.CLAY
        PEATY -> SoilType.PEATY
        CHALKY -> SoilType.CHALKY
        SILTY -> SoilType.SILTY
    }

    companion object {
        fun fromNetwork(network: SoilType): GrpcSoilType = when (network) {
            SoilType.SANDY -> SANDY
            SoilType.LOAMY -> LOAMY
            SoilType.CLAY -> CLAY
            SoilType.PEATY -> PEATY
            SoilType.CHALKY -> CHALKY
            SoilType.SILTY -> SILTY
            SoilType.UNRECOGNIZED -> throw IllegalArgumentException("Unknown SoilType: $network")
        }
    }
}