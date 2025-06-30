package com.tometrics.api.services.servicediscovery.service

import com.tometrics.api.services.commongrpc.services.ServiceDiscoveryGrpcService
import com.tometrics.api.common.domain.models.ServiceInfo
import com.tometrics.api.common.domain.models.ServiceType
import io.ktor.util.collections.*

interface ServiceDiscoveryService : ServiceDiscoveryGrpcService {

}

class DefaultServiceDiscoveryService : ServiceDiscoveryService {

    private val registry = ConcurrentMap<ServiceType, ServiceInfo>()

    override suspend fun register(info: ServiceInfo) {
        registry.put(info.type, info)
    }

    override suspend fun get(type: ServiceType): ServiceInfo? =
        registry[type]
}
