package com.tometrics.api.services.commongrpc.services

import com.tometrics.api.common.domain.models.ServiceInfo
import com.tometrics.api.common.domain.models.ServiceType
import com.tometrics.api.services.commongrpc.models.servicediscovery.fromNetwork
import com.tometrics.api.services.commongrpc.models.servicediscovery.toNetwork
import com.tometrics.api.services.protos.ServiceDiscoveryGrpcServiceGrpcKt
import com.tometrics.api.services.protos.getServiceInfoRequest
import com.tometrics.api.services.protos.serviceInfoOrNull

interface ServiceDiscoveryGrpcClient {
    suspend fun register(info: ServiceInfo)
    suspend fun get(type: ServiceType): ServiceInfo?
}

class DefaultServiceDiscoveryGrpcClient(
    private val client: ServiceDiscoveryGrpcServiceGrpcKt.ServiceDiscoveryGrpcServiceCoroutineStub,
) : ServiceDiscoveryGrpcClient {

    override suspend fun register(info: ServiceInfo) {
        client.register(info.toNetwork())
    }

    override suspend fun get(type: ServiceType): ServiceInfo? =
        client.get(getServiceInfoRequest { serviceType = type.toNetwork() })
            .serviceInfoOrNull
            ?.fromNetwork()

}