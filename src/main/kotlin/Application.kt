package com.sproutscout.api

import com.sproutscout.api.di.configureDI
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDI()
    configureMonitoring()
    configureHTTP()
    configureSecurity()
    configureRouting()
}
