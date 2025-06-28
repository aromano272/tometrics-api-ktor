package com.tometrics.api.services.user.services

import com.tometrics.api.services.protos.*


class UserGrpcService(
    private val userService: UserService,
) : UserGrpcServiceGrpcKt.UserGrpcServiceCoroutineImplBase() {

    override suspend fun validateUserIds(request: ValidateUserIdsRequest): ValidateUserIdsResponse {
        return userService.validateUserIds(request.userIdsList.toSet()).let {
            validateUserIdsResponse {
                when (it) {
                    ValidateUsersResult.Success -> success = validateUsersSuccess {}
                    is ValidateUsersResult.UserIdsNotFound -> notFound = validateUsersNotFound {
                        missingUserIds += it.missingUserIds
                    }
                }
            }
        }
    }

}