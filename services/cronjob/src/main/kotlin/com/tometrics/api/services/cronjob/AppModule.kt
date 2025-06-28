package com.tometrics.api.services.cronjob


import com.tometrics.api.services.cronjob.services.CronjobService
import com.tometrics.api.services.cronjob.services.DefaultCronjobService
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.util.logging.*
import org.koin.dsl.module

fun appModule(application: Application) = module {

    single<Dotenv> {
        dotenv {
            ignoreIfMissing = true
        }
    }

    // TODO(aromano): move this to commonservice module, along with dotenv for eg.
    factory<Logger> {
        application.environment.log
    }

}

val serviceModule = module {

    single<CronjobService> {
        DefaultCronjobService(
            logger = get(),
            gardenGrpcClient = get(),
            userGrpcClient = get(),
        )
    }

}


