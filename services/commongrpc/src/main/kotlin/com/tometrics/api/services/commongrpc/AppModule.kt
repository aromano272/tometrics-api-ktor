package com.tometrics.api.services.commongrpc

import com.tometrics.api.services.commongrpc.services.*
import com.tometrics.api.services.protos.GardenGrpcServiceGrpcKt
import com.tometrics.api.services.protos.ServiceDiscoveryGrpcServiceGrpcKt
import com.tometrics.api.services.protos.UserGrpcServiceGrpcKt
import io.grpc.ManagedChannelBuilder
import org.koin.dsl.module

val commonServicesGrpcModule = module {

    single<UserGrpcClient> {
        val channel = ManagedChannelBuilder
            .forAddress("localhost", 9082)
            .usePlaintext()
            .build()

        DefaultUserGrpcClient(
            service = UserGrpcServiceGrpcKt.UserGrpcServiceCoroutineStub(channel)
        )
    }

    single<GardenGrpcClient> {
        val channel = ManagedChannelBuilder
            .forAddress("localhost", 9086)
            .usePlaintext()
            .build()

        DefaultGardenGrpcClient(
            service = GardenGrpcServiceGrpcKt.GardenGrpcServiceCoroutineStub(channel)
        )
    }

    single<ServiceDiscoveryGrpcClient> {
        val channel = ManagedChannelBuilder
            .forAddress("localhost", 9083)
            .usePlaintext()
            .build()

        DefaultServiceDiscoveryGrpcClient(
            service = ServiceDiscoveryGrpcServiceGrpcKt.ServiceDiscoveryGrpcServiceCoroutineStub(channel)
        )
    }

}
