package com.tometrics.api.services.commongrpc.models.garden

import com.tometrics.api.services.protos.PlantRef

data class GrpcPlantRef(
    val id: Int,
    val name: String
) {
    fun toNetwork(): PlantRef = PlantRef.newBuilder()
        .setId(id)
        .setName(name)
        .build()

    companion object {
        fun fromNetwork(network: PlantRef): GrpcPlantRef = GrpcPlantRef(
            id = network.id,
            name = network.name
        )
    }
}