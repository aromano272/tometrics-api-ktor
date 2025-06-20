package com.tometrics.api.common.domain.models

sealed class CommonError : RuntimeException()

data class ValidationError(
    val errors: List<String>,
) : CommonError()

data class UnauthorizedError(
    override val message: String
) : CommonError()

