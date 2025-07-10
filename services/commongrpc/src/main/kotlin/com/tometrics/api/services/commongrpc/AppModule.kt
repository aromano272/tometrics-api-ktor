package com.tometrics.api.services.commongrpc

import com.tometrics.api.common.domain.models.ServiceType
import com.tometrics.api.services.commongrpc.services.*
import com.tometrics.api.services.protos.*
import io.grpc.ManagedChannelBuilder
import org.koin.dsl.module

val commonServicesGrpcModule = module {

    single<UserGrpcClient> {
        DefaultUserGrpcClient(
            client = GrpcLazyClient(
                stubConstructor = {
                    UserGrpcServiceGrpcKt.UserGrpcServiceCoroutineStub(it)
                },
                type = ServiceType.USER,
                serviceDiscovery = get(),
            )
        )
    }

    single<GardenGrpcClient> {
        DefaultGardenGrpcClient(
            client = GrpcLazyClient(
                stubConstructor = {
                    GardenGrpcServiceGrpcKt.GardenGrpcServiceCoroutineStub(it)
                },
                type = ServiceType.GARDEN,
                serviceDiscovery = get(),
            )
        )
    }

    single<SocialGraphGrpcClient> {
        DefaultSocialGraphGrpcClient(
            client = GrpcLazyClient(
                stubConstructor = {
                    SocialGraphGrpcServiceGrpcKt.SocialGraphGrpcServiceCoroutineStub(it)
                },
                type = ServiceType.SOCIALGRAPH,
                serviceDiscovery = get(),
            )
        )
    }

    single<MediaGrpcClient> {
        DefaultMediaGrpcClient(
            client = GrpcLazyClient(
                stubConstructor = {
                    MediaGrpcServiceGrpcKt.MediaGrpcServiceCoroutineStub(it)
                },
                type = ServiceType.MEDIA,
                serviceDiscovery = get(),
            )
        )
    }

    single<ServiceDiscoveryGrpcClient> {
        val channel = ManagedChannelBuilder
            .forAddress("localhost", 9083)
            .usePlaintext()
            .build()

        DefaultServiceDiscoveryGrpcClient(
            client = ServiceDiscoveryGrpcServiceGrpcKt.ServiceDiscoveryGrpcServiceCoroutineStub(channel)
        )
    }

}
