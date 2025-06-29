package com.tometrics.api.services.email


import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheFactory
import com.tometrics.api.services.email.services.EmailService
import com.tometrics.api.services.email.services.EmailTemplateRenderer
import com.tometrics.api.services.email.services.MailgunEmailService
import com.tometrics.api.services.email.services.MustacheEmailTemplateRenderer
import org.koin.dsl.module

val appModule = module {

    single<MustacheFactory> {
        DefaultMustacheFactory("templates")
    }

}

val serviceModule = module {

    single<EmailService> {
        MailgunEmailService(
            dotenv = get(),
            httpClient = get(),
            emailTemplateRenderer = get(),
            logger = get(),
        )
    }

    single<EmailTemplateRenderer> {
        MustacheEmailTemplateRenderer(
            mustacheFactory = get()
        )
    }

}


