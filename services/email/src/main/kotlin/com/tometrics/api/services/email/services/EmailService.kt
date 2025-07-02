package com.tometrics.api.services.email.services

import com.tometrics.api.services.email.services.templates.Template
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.logging.*

interface EmailService {
    suspend fun sendEmail(to: String, subject: String, template: Template)
}

class MailgunEmailService(
    dotenv: Dotenv,
    private val httpClient: HttpClient,
    private val emailTemplateRenderer: EmailTemplateRenderer,
    private val logger: Logger,
) : EmailService {

    private val apiKey = dotenv["MAILGUN_API_KEY"]
    private val domain = dotenv["MAILGUN_DOMAIN"]
    private val fromEmail = "Tometrics <noreply@tometrics.com>"

    override suspend fun sendEmail(to: String, subject: String, template: Template) {
        val url = "https://api.mailgun.net/v3/$domain/messages"

        val htmlBody = emailTemplateRenderer.render(template)

        val response = httpClient.submitForm(
            url = url,
            formParameters = Parameters.build {
                append("from", fromEmail)
                append("to", to)
                append("subject", subject)
                append("html", htmlBody)
            }
        ) {
            basicAuth("api", apiKey)
        }

        if (!response.status.isSuccess()) {
            logger.error(response.bodyAsText())
            throw RuntimeException("Failed to send email: ${response.status}")
        }
    }

}