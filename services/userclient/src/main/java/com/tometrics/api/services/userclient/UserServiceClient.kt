package com.tometrics.api.services.userclient

import com.tometrics.api.common.domain.models.UserId
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
    private val httpClient: HttpClient,
) : UserServiceClient {

    override suspend fun validateUserIds(vararg userIds: UserId) {
//        val response = httpClient.post("http://tometrics-user:8082/internal/user/validate-users") {
        val response = httpClient.post("http://tometrics-user:8082/internal/user/validate-users") {
            setBody(ValidateUsersRequest(userIds.toSet()))
        }
        println("boomshaka")
        println(response)
    }

}
