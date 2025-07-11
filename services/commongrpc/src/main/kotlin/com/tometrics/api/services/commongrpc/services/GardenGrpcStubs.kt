package com.tometrics.api.services.commongrpc.services

import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.commongrpc.models.garden.GrpcPlanting
import com.tometrics.api.services.protos.GardenGrpcServiceGrpcKt
import com.tometrics.api.services.protos.empty

interface GardenGrpcClient {
    suspend fun getAllReadyForHarvestToday(): Map<UserId, List<GrpcPlanting>>
}

class DefaultGardenGrpcClient(
    private val client: GrpcLazyClient<GardenGrpcServiceGrpcKt.GardenGrpcServiceCoroutineStub>,
) : GardenGrpcClient {

    override suspend fun getAllReadyForHarvestToday(): Map<UserId, List<GrpcPlanting>> =
        client.await().getAllReadyForHarvestToday(empty {}).let { response ->
            response.resultsMap.mapValues { (_, plantings) ->
                plantings.plantingList.map { planting ->
                    GrpcPlanting.Companion.fromNetwork(planting)
                }
            }
        }

}