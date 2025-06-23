package com.tometrics.api.services.userclient

import com.tometrics.api.common.domain.models.UserId
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable

interface UserServiceClient {

    suspend fun validateUserIds(vararg userIds: UserId)

}

@Serializable
data class ValidateUsersRequest(
    val userIds: Set<UserId>,
)

class HttpUserServiceClient(
    private val dotenv: Dotenv,
    private val httpClient: HttpClient,
) : UserServiceClient {

    override suspend fun validateUserIds(vararg userIds: UserId) {
        val runningLocally = dotenv["RUNNING_LOCALLY"] == "1"
        val host = if (runningLocally) "localhost" else "tometrics-user:8082"
        httpClient.post("http://$host/internal/user/validate-users") {
            setBody(ValidateUsersRequest(userIds.toSet()))
        }
    }

}
