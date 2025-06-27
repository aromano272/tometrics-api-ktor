package com.tometrics.api.services.commonclient

import com.tometrics.api.services.protos.UserGrpcServiceGrpcKt
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import io.grpc.ManagedChannelBuilder
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

val serviceCommonClientModule = module {

    single<Dotenv> {
        dotenv {
            ignoreIfMissing = true
        }
    }

    single {
        val channel = ManagedChannelBuilder.forAddress("localhost", 9082)
            .usePlaintext()
            .build()

        UserGrpcServiceGrpcKt.UserGrpcServiceCoroutineStub(channel)
    }

}
