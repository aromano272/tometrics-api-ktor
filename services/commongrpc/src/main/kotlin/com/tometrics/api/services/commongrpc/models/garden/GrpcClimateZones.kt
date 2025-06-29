package com.tometrics.api.services.commongrpc.models.garden

import com.tometrics.api.services.protos.ClimateZones

data class GrpcClimateZones(
    val temperate: List<Int>,
    val mediterranean: List<Int>,
    val continental: List<Int>,
    val tropical: List<Int>,
    val arid: List<Int>
) {
    fun toNetwork(): ClimateZones = ClimateZones.newBuilder()
        .addAllTemperate(temperate)
        .addAllMediterranean(mediterranean)
        .addAllContinental(continental)
        .addAllTropical(tropical)
        .addAllArid(arid)
        .build()

    companion object {
        fun fromNetwork(network: ClimateZones): GrpcClimateZones = GrpcClimateZones(
            temperate = network.temperateList,
            mediterranean = network.mediterraneanList,
            continental = network.continentalList,
            tropical = network.tropicalList,
            arid = network.aridList
        )
    }
}