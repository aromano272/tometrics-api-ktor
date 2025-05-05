package com.tometrics.api.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.server.config.*
import java.time.Instant

interface JwtService {
    fun create(userId: Int, anon: Boolean): String
}

class DefaultJwtService(
    private val jwtAudience: String,
    private val jwtDomain: String,
    private val jwtSecret: String,
    private val developmentMode: Boolean,
) : JwtService {

    constructor(
        config: ApplicationConfig,
        dotenv: Dotenv,
        developmentMode: Boolean,
    ) : this(
        jwtAudience = config.property("jwt.audience").getString(),
        jwtDomain = config.property("jwt.domain").getString(),
        jwtSecret = dotenv["JWT_SECRET"],
        developmentMode = developmentMode,
    )

    override fun create(userId: Int, anon: Boolean): String = JWT.create()
        .withAudience(jwtAudience)
        .withIssuer(jwtDomain)
        .withClaim("userId", userId)
        .withClaim("anon", anon)
        .withExpiresAt(getNewAccessTokenExpiry())
        .sign(Algorithm.HMAC256(jwtSecret))

    private fun getNewAccessTokenExpiry(): Instant {
        val expirationMillis = when {
            developmentMode -> 30 * 24 * 60 * 60 * 1000L // 30 days
            else -> 60 * 60 * 1000L // 1 hour in prod
        }
        return Instant.ofEpochMilli(System.currentTimeMillis() + expirationMillis)
    }

}
