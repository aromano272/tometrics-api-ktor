package com.tometrics.api.services.servicediscovery

import com.tometrics.api.services.servicediscovery.service.DefaultService
import com.tometrics.api.services.servicediscovery.service.Service
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import org.koin.dsl.module

val appModule = module {

    single<Dotenv> {
        dotenv {
            ignoreIfMissing = true
        }
    }

}

val serviceModule = module {

    single<Service> {
        DefaultService()
    }

}
