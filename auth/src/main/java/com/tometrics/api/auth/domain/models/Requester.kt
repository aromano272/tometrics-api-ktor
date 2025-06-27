package com.tometrics.api.auth.domain.models

import com.tometrics.api.common.domain.models.UnauthorizedError
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class Requester(
    val userId: Int,
)

private fun JWTPrincipal.asRequester(): Requester = Requester(
    userId = getClaim("userId", Int::class)
        ?: throw UnauthorizedError("userId missing from token"),
)

fun RoutingCall.requireRequester(): Requester = principal<JWTPrincipal>()?.asRequester()
    ?: throw UnauthorizedError("missing jwt")

fun RoutingCall.requester(): Requester? = principal<JWTPrincipal>()?.asRequester()