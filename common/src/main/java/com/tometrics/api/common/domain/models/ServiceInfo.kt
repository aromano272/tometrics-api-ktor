package com.tometrics.api.common.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ServiceInfo(
    val prefix: String,
    val host: String,
    val port: Int,
    val type: ServiceType,
) {
    val grpcPort = port + 1000
}

enum class ServiceType {
    SERVICEDISCOVERY,
    CRONJOB,
    EMAIL,
    GARDEN,
    SOCIALFEED,
    SOCIALGRAPH,
    USER,
    MEDIA,
}