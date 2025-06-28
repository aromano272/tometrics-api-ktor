package com.tometrics.api.services.user.domain.models

import com.tometrics.api.common.domain.models.UserId

sealed class ServiceError : RuntimeException()

data class UserIdsNotFoundError(val missingIds: Set<UserId>) : ServiceError()
// TODO(aromano): make this more meaningful rather than an http-inspired error
data class ConflictException(override val message: String) : ServiceError()
data class UnauthorizedException(override val message: String) : ServiceError()
data class BadRequestException(override val message: String) : ServiceError()
