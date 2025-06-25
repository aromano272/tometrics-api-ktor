package com.tometrics.api.services.userclient

import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.userrpc.UserRpcService
import com.tometrics.api.userrpc.ValidateUsersResult
import kotlinx.serialization.Serializable

interface UserServiceClient {

    suspend fun validateUserIds(vararg userIds: UserId): ValidateUsersResult

}

class HttpUserServiceClient(
    private val rpcService: UserRpcService,
) : UserServiceClient {

    override suspend fun validateUserIds(vararg userIds: Int): ValidateUsersResult =
        rpcService.validateUserIds(userIds.toSet())

//    override suspend fun validateUserIds(vararg userIds: UserId): ValidateUsersResult {
//        val runningLocally = dotenv["RUNNING_LOCALLY"] == "1"
//        val host = if (runningLocally) "localhost" else "tometrics-user:8082"
//        val response = httpClient.post("http://$host/internal/user/validate-users") {
//            setBody(ValidateUsersRequest(userIds.toSet()))
//        }
//
//        if (!response.status.isSuccess()) {
//            val error = response.body<ErrorResponse>()
//            when (error.code) {
//                "USER_IDS_NOT_FOUND" -> throw UserServiceClientError.UserIdsNotFound(error.error)
//                else -> throw UserServiceClientError.Unknown(error.error)
//            }
//        }
//    }

}

@Serializable
data class ValidateUsersRequest(
    val userIds: Set<UserId>,
)

sealed class UserServiceClientError : RuntimeException() {
    data class UserIdsNotFound(override val message: String) : UserServiceClientError()
    data class Unknown(override val message: String) : UserServiceClientError()
}

