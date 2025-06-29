package com.tometrics.api.services.commongrpc

import com.tometrics.api.services.commongrpc.services.DefaultGardenGrpcClient
import com.tometrics.api.services.commongrpc.services.DefaultUserGrpcClient
import com.tometrics.api.services.commongrpc.services.GardenGrpcClient
import com.tometrics.api.services.commongrpc.services.UserGrpcClient
import com.tometrics.api.services.protos.GardenGrpcServiceGrpcKt
import com.tometrics.api.services.protos.UserGrpcServiceGrpcKt
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import io.grpc.ManagedChannelBuilder
import org.koin.dsl.module

val commonServicesGrpcModule = module {

    single<Dotenv> {
        dotenv {
            ignoreIfMissing = true
        }
    }

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

}
