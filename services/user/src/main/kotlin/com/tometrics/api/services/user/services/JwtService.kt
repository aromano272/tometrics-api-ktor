package com.tometrics.api.services.user.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.Dotenv
import java.time.Instant

interface JwtService {
    fun create(userId: Int, anon: Boolean): String
}

class DefaultJwtService(
    private val developmentMode: Boolean,
    private val jwtAudience: String,
    private val jwtDomain: String,
    private val jwtSecret: String,
) : JwtService {

    constructor(
        dotenv: Dotenv,
    ) : this(
        developmentMode = dotenv["DEVELOPMENT"] != null,
        jwtAudience = dotenv["JWT_AUDIENCE"],
        jwtDomain = dotenv["JWT_DOMAIN"],
        jwtSecret = dotenv["JWT_SECRET"],
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
