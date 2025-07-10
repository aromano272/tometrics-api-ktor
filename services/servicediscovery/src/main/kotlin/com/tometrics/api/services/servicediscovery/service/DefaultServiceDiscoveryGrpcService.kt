package com.tometrics.api.services.servicediscovery.service

import com.google.protobuf.empty
import com.tometrics.api.services.commongrpc.models.servicediscovery.fromNetwork
import com.tometrics.api.services.commongrpc.models.servicediscovery.toNetwork
import com.tometrics.api.services.protos.*

class DefaultServiceDiscoveryGrpcService(
    private val service: ServiceDiscoveryService,
) : ServiceDiscoveryGrpcServiceGrpcKt.ServiceDiscoveryGrpcServiceCoroutineImplBase() {

    override suspend fun register(request: ServiceInfo): com.google.protobuf.Empty {
        val info = request.fromNetwork() ?: throw UnsupportedOperationException("couldn't parse $request")
        service.register(info)
        return empty {}
    }

    override suspend fun get(request: GetServiceInfoRequest): GetServiceInfoResponse {
        val type = request.serviceType.fromNetwork() ?: throw UnsupportedOperationException("couldn't parse $request")
        val info = service.get(type) ?: return getServiceInfoResponse {}
        return getServiceInfoResponse { serviceInfo = info.toNetwork() }
    }

}
