package com.tometrics.api.domain.models

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
        ?: throw UnauthorizedException("userId missing from token"),
)

fun RoutingCall.requireRequester(): Requester = principal<JWTPrincipal>()?.asRequester()
    ?: throw UnauthorizedException("missing jwt")

fun RoutingCall.requester(): Requester? = principal<JWTPrincipal>()?.asRequester()