package com.tometrics.api.services.commongrpc.models.servicediscovery

import com.tometrics.api.services.protos.ServiceInfo
import com.tometrics.api.services.protos.ServiceType
import com.tometrics.api.common.domain.models.ServiceInfo as DomainServiceInfo
import com.tometrics.api.common.domain.models.ServiceType as DomainServiceType

fun ServiceInfo.fromNetwork(): DomainServiceInfo? = type.fromNetwork()?.let {
    DomainServiceInfo(
        prefix = prefix,
        host = host,
        port = port,
        type = it,
    )
}

fun DomainServiceInfo.toNetwork(): ServiceInfo = ServiceInfo.newBuilder()
    .setPrefix(prefix)
    .setHost(host)
    .setPort(port)
    .setType(type.toNetwork())
    .build()

fun ServiceType.fromNetwork() = when (this) {
    ServiceType.SERVICEDISCOVERY -> DomainServiceType.SERVICEDISCOVERY
    ServiceType.CRONJOB -> DomainServiceType.CRONJOB
    ServiceType.EMAIL -> DomainServiceType.EMAIL
    ServiceType.GARDEN -> DomainServiceType.GARDEN
    ServiceType.SOCIALFEED -> DomainServiceType.SOCIALFEED
    ServiceType.SOCIALGRAPH -> DomainServiceType.SOCIALGRAPH
    ServiceType.USER -> DomainServiceType.USER
    ServiceType.MEDIA -> DomainServiceType.MEDIA
    ServiceType.UNRECOGNIZED -> null
}

fun DomainServiceType.toNetwork() = when (this) {
    DomainServiceType.SERVICEDISCOVERY -> ServiceType.SERVICEDISCOVERY
    DomainServiceType.CRONJOB -> ServiceType.CRONJOB
    DomainServiceType.EMAIL -> ServiceType.EMAIL
    DomainServiceType.GARDEN -> ServiceType.GARDEN
    DomainServiceType.SOCIALFEED -> ServiceType.SOCIALFEED
    DomainServiceType.SOCIALGRAPH -> ServiceType.SOCIALGRAPH
    DomainServiceType.USER -> ServiceType.USER
    DomainServiceType.MEDIA -> ServiceType.MEDIA
}