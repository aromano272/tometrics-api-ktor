package com.tometrics.api.services.commongrpc.models.garden

import com.tometrics.api.services.protos.YieldUnit

enum class GrpcYieldUnit {
    UNIT,
    KG,
    GRAMS,
    LB,
    OZ;

    fun toNetwork(): YieldUnit = when (this) {
        UNIT -> YieldUnit.UNIT
        KG -> YieldUnit.KG
        GRAMS -> YieldUnit.GRAMS
        LB -> YieldUnit.LB
        OZ -> YieldUnit.OZ
    }

    companion object {
        fun fromNetwork(network: YieldUnit): GrpcYieldUnit = when (network) {
            YieldUnit.UNIT -> UNIT
            YieldUnit.KG -> KG
            YieldUnit.GRAMS -> GRAMS
            YieldUnit.LB -> LB
            YieldUnit.OZ -> OZ
            YieldUnit.UNRECOGNIZED -> throw IllegalArgumentException("Unknown YieldUnit: $network")
        }
    }
}
