package com.tometrics.api.servicediscovery.service

import io.ktor.util.collections.*
import kotlinx.serialization.Serializable

interface Service {

    suspend fun register(info: ServiceInfo)
    suspend fun get(name: String): ServiceInfo?

}

class DefaultService : Service {

    private val registry = ConcurrentMap<String, ServiceInfo>()

    override suspend fun register(info: ServiceInfo) {
        registry.put(info.name, info)
    }

    override suspend fun get(name: String): ServiceInfo? =
        registry[name]
}

@Serializable
data class ServiceInfo(
    val name: String,
    val host: String,
    val port: Int,
)
