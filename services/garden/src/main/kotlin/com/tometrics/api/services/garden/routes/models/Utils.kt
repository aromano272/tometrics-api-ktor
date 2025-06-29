package com.tometrics.api.services.garden.routes.models

import com.tometrics.api.common.domain.models.BadRequestError
import io.ktor.http.*

inline fun <reified T : Enum<T>> Parameters.getAndValidateEnum(name: String): T? =
    get(name)?.let {
        enumValueOfOrThrowBadRequest<T>(it, name)
    }

inline fun <reified T : Enum<T>> Parameters.getAllAndValidateEnum(name: String): List<T> =
    getAll(name)?.map {
        enumValueOfOrThrowBadRequest(it, name)
    } ?: emptyList()

inline fun <reified T : Enum<T>> enumValueOfOrThrowBadRequest(name: String, field: String): T = try {
    enumValueOf<T>(name.uppercase())
} catch (ex: Exception) {
    throw BadRequestError("Invalid $field value, expected: ${T::class.java.enumConstants.toList()}, got: ${name.uppercase()}")
}

