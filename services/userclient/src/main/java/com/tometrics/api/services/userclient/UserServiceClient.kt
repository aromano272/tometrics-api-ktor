package com.tometrics.api.services.userclient

import com.tometrics.api.common.domain.models.ErrorResponse
import com.tometrics.api.common.domain.models.UserId
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

interface UserServiceClient {

    suspend fun validateUserIds(vararg userIds: UserId)

}

class HttpUserServiceClient(
    private val dotenv: Dotenv,
    private val httpClient: HttpClient,
) : UserServiceClient {

    override suspend fun validateUserIds(vararg userIds: UserId) {
        val runningLocally = dotenv["RUNNING_LOCALLY"] == "1"
        val host = if (runningLocally) "localhost" else "tometrics-user:8082"
        val response = httpClient.post("http://$host/internal/user/validate-users") {
            setBody(ValidateUsersRequest(userIds.toSet()))
        }

        if (!response.status.isSuccess()) {
            val error = response.body<ErrorResponse>()
            when (error.code) {
                "USER_IDS_NOT_FOUND" -> throw UserServiceClientError.UserIdsNotFound(error.error)
                else -> throw UserServiceClientError.Unknown(error.error)
            }
        }
    }

}

@Serializable
data class ValidateUsersRequest(
    val userIds: Set<UserId>,
)

sealed class UserServiceClientError : RuntimeException() {
    data class UserIdsNotFound(override val message: String) : UserServiceClientError()
    data class Unknown(override val message: String) : UserServiceClientError()
}

