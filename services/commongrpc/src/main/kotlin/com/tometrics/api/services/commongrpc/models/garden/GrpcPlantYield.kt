package com.tometrics.api.services.commongrpc.models.garden

import com.tometrics.api.services.protos.PlantYield

data class GrpcPlantYield(
    val from: Float,
    val to: Float,
    val unit: GrpcYieldUnit
) {
    fun toNetwork(): PlantYield = PlantYield.newBuilder()
        .setFrom(from)
        .setTo(to)
        .setUnit(unit.toNetwork())
        .build()

    companion object {
        fun fromNetwork(network: PlantYield): GrpcPlantYield = GrpcPlantYield(
            from = network.from,
            to = network.to,
            unit = GrpcYieldUnit.fromNetwork(network.unit)
        )
    }
}