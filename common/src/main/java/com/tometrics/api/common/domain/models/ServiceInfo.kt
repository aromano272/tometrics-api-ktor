package com.tometrics.api.common.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class ServiceInfo(
    val prefix: String,
    val host: String,
    val port: Int,
    val type: ServiceType,
)

enum class ServiceType {
    SERVICEDISCOVERY,
    CRONJOB,
    EMAIL,
    GARDEN,
    SOCIALGRAPH,
    USER,
}