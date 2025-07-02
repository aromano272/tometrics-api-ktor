package com.tometrics.api.services.cronjob


import com.tometrics.api.services.cronjob.services.CronjobService
import com.tometrics.api.services.cronjob.services.DefaultCronjobService
import org.koin.dsl.module

val serviceModule = module {

    single<CronjobService> {
        DefaultCronjobService(
            logger = get(),
            gardenGrpcClient = get(),
            userGrpcClient = get(),
        )
    }

}


