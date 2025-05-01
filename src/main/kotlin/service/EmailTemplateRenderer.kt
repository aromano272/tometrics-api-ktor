package com.tometrics.api.service

import com.tometrics.api.service.templates.Template
import com.github.mustachejava.MustacheFactory
import java.io.StringWriter

interface EmailTemplateRenderer {
    fun render(data: Template): String
}

class MustacheEmailTemplateRenderer(
    private val mustacheFactory: MustacheFactory,
) : EmailTemplateRenderer {
    private val templateDir: String = "emails"

    override fun render(data: Template): String {
        val writer = StringWriter()

        mustacheFactory.compile("$templateDir/${data._filename}").execute(writer, data)

        return writer.toString()
    }
}