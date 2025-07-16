package com.tometrics.api.services.email

import com.tometrics.api.common.domain.models.ServiceInfo
import com.tometrics.api.common.domain.models.ServiceType
import com.tometrics.api.services.commonservice.Message
import com.tometrics.api.services.commonservice.commonModule
import com.tometrics.api.services.commonservice.launchRabbitMQConsumer
import com.tometrics.api.services.commonservice.registerEvent
import com.tometrics.api.services.email.services.EmailService
import io.github.smiley4.ktoropenapi.route
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import org.koin.core.KoinApplication
import org.koin.ktor.ext.get

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    commonModule(
        serviceInfo = ServiceInfo(
            prefix = "/email",
            host = "localhost",
            port = this.environment.config.port,
            type = ServiceType.EMAIL,
        ),
        configureDI = configureDI,
    )
    configureRouting()

    configureRabbitMQ()
}

fun Application.configureRabbitMQ() {
    val emailService: EmailService = get()
    launchRabbitMQConsumer("email.service.queue") {
        registerEvent<Message.PostCreated> {
            emailService.handlemessage(it)
        }

        registerEvent<Message.PostCreated> {
            emailService.handlemessage(it)
        }
    }
}

val configureDI: KoinApplication.() -> Unit
    get() = {
        modules(
            appModule,
            serviceModule,
        )
    }

fun Application.configureRouting() {
    routing {
        route("/api/v1") {
        }
    }
}