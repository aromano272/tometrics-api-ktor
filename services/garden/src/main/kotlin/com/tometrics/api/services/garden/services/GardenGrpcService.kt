package com.tometrics.api.services.garden.services

import com.tometrics.api.services.commongrpc.services.GardenGrpcService
import com.tometrics.api.services.protos.*

class DefaultGardenGrpcService(
    private val service: GardenGrpcService,
) : GardenGrpcServiceGrpcKt.GardenGrpcServiceCoroutineImplBase() {
    override suspend fun getAllReadyForHarvestToday(request: Empty): GetAllReadyForHarvestTodayResponse =
        service.getAllReadyForHarvestToday().let { response ->
            getAllReadyForHarvestTodayResponse {
                val network = response.mapValues { (_, plantings) ->
                    GetAllReadyForHarvestTodayResponseKt.listPlanting {
                        planting += plantings.map { it.toNetwork() }
                    }
                }
                results.putAll(network)
            }
        }

}
