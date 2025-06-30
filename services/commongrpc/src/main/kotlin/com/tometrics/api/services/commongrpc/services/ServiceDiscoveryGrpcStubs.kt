package com.tometrics.api.services.commongrpc.services

import com.tometrics.api.common.domain.models.ServiceInfo
import com.tometrics.api.common.domain.models.ServiceType
import com.tometrics.api.services.commongrpc.models.servicediscovery.fromNetwork
import com.tometrics.api.services.commongrpc.models.servicediscovery.toNetwork
import com.tometrics.api.services.protos.ServiceDiscoveryGrpcServiceGrpcKt
import com.tometrics.api.services.protos.getServiceInfoRequest
import com.tometrics.api.services.protos.serviceInfoOrNull

interface ServiceDiscoveryGrpcService {
    suspend fun register(info: ServiceInfo)
    suspend fun get(type: ServiceType): ServiceInfo?
}

interface ServiceDiscoveryGrpcClient : ServiceDiscoveryGrpcService

class DefaultServiceDiscoveryGrpcClient(
    private val service: ServiceDiscoveryGrpcServiceGrpcKt.ServiceDiscoveryGrpcServiceCoroutineStub,
) : ServiceDiscoveryGrpcClient {

    override suspend fun register(info: ServiceInfo) {
        service.register(info.toNetwork())
    }

    override suspend fun get(type: ServiceType): ServiceInfo? =
        service.get(getServiceInfoRequest { serviceType = type.toNetwork() })
            .serviceInfoOrNull
            ?.fromNetwork()

}