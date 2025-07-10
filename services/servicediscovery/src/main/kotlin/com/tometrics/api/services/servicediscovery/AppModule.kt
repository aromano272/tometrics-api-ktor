package com.tometrics.api.services.servicediscovery

import com.tometrics.api.services.servicediscovery.service.DefaultServiceDiscoveryGrpcService
import com.tometrics.api.services.servicediscovery.service.DefaultServiceDiscoveryService
import com.tometrics.api.services.servicediscovery.service.ServiceDiscoveryService
import org.koin.dsl.module

val serviceModule = module {

    single<DefaultServiceDiscoveryGrpcService> {
        DefaultServiceDiscoveryGrpcService(
            service = get(),
        )
    }

    single<ServiceDiscoveryService> {
        DefaultServiceDiscoveryService()
    }

}
