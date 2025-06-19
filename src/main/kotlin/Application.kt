package com.tometrics.api

import com.tometrics.api.auth.configureSecurity
import com.tometrics.api.di.configureDI
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
