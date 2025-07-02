package com.tometrics.api.services.commongrpc.services

import com.tometrics.api.common.domain.models.ServiceType
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.kotlin.AbstractCoroutineStub
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class GrpcLazyClient<T : AbstractCoroutineStub<T>>(
    private val stubConstructor: (ManagedChannel) -> T,
    private val type: ServiceType,
    private val serviceDiscovery: ServiceDiscoveryGrpcClient,
) {
    private val mutex = Mutex()
    private var stub: T? = null

    suspend fun await(): T = mutex.withLock {
        stub?.let { return@withLock it }
        val info = serviceDiscovery.get(type)
            ?: throw IllegalStateException("Service '$type' not available")

        val channel = ManagedChannelBuilder
            .forAddress(info.host, info.grpcPort)
            .usePlaintext() // or .useTransportSecurity() in prod
            .build()

        stubConstructor(channel).also {
            stub = it
        }
    }
}