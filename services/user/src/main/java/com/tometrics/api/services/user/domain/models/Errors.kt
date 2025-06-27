package com.tometrics.api.services.user.domain.models

import com.tometrics.api.common.domain.models.UserId

sealed class ServiceError : RuntimeException()

data class UserIdsNotFoundError(val missingIds: Set<UserId>) : ServiceError()