package com.sproutscout.api

import com.sproutscout.api.di.configureDI
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.Application

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDI()
    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureSecurity()
    configureRouting()
}
