package com.tometrics.api.services.garden.domain.models

sealed class ServiceError : RuntimeException()

class NotFoundError(message: String) : ServiceError()
class BadRequestError(message: String) : ServiceError()
class UnauthorizedError(message: String) : ServiceError()
class ConflictError(message: String) : ServiceError()
class ForbiddenError(message: String) : ServiceError()
