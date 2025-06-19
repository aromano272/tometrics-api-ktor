package com.tometrics.api.services.socialgraph

import com.tometrics.api.services.socialgraph.db.Database
import com.tometrics.api.services.socialgraph.service.SocialGraphService
import io.ktor.server.application.*
import org.koin.dsl.module

val appModule = module {
    // Database
    single { (environment: ApplicationEnvironment) -> Database.init(environment) }

    // Services
    single { SocialGraphService(get()) }
}