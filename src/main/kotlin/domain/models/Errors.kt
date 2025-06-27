package com.tometrics.api.domain.models

sealed class ApiException(message: String) : RuntimeException(message)

class NotFoundException(message: String) : ApiException(message)
class BadRequestException(message: String) : ApiException(message)
class UnauthorizedException(message: String) : ApiException(message)
class ConflictException(message: String) : ApiException(message)
class ForbiddenException(message: String) : ApiException(message)
