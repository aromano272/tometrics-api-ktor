package com.tometrics.api.common.domain.models

import kotlinx.serialization.Serializable

sealed class CommonError : RuntimeException()

data class ValidationError(
    val errors: List<String>,
) : CommonError()

data class NotFoundError(override val message: String) : CommonError()
data class BadRequestError(override val message: String) : CommonError()
data class UnauthorizedError(override val message: String) : CommonError()
data class ConflictError(override val message: String) : CommonError()
data class ForbiddenError(override val message: String) : CommonError()

@Serializable
data class ErrorResponse(
    val error: String,
    val errors: List<String>? = null,
    val code: String? = null,
)

