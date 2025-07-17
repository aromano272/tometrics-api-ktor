package com.tometrics.api.services.commonservice

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.JWTVerifier
import io.github.cdimascio.dotenv.Dotenv
import java.time.Instant

interface JwtService {
    val jwtAudience: String
    val jwtDomain: String
    val jwtRealm: String

    fun createSignedJWT(userId: Int, anon: Boolean): String
    fun createVerifier(): JWTVerifier
    fun verifyOrNull(token: String): DecodedJWT?
}

class DefaultJwtService(
    dotenv: Dotenv,
) : JwtService {

    private val developmentMode: Boolean = dotenv["DEVELOPMENT"] != null
    override val jwtAudience: String = dotenv["JWT_AUDIENCE"]
    override val jwtDomain: String = dotenv["JWT_DOMAIN"]
    override val jwtRealm: String = dotenv["JWT_REALM"]
    private val jwtSecret: String = dotenv["JWT_SECRET"]
    private val algo = Algorithm.HMAC256(jwtSecret)

    override fun createSignedJWT(userId: Int, anon: Boolean): String = JWT.create()
        .withAudience(jwtAudience)
        .withIssuer(jwtDomain)
        .withClaim("userId", userId)
        .withClaim("anon", anon)
        .withExpiresAt(getNewAccessTokenExpiry())
        .sign(algo)

    override fun createVerifier(): JWTVerifier = JWT
        .require(algo)
        .withAudience(jwtAudience)
        .withIssuer(jwtDomain)
        .build()

    override fun verifyOrNull(token: String): DecodedJWT? =
        try {
            val verifier = createVerifier()
            verifier.verify(token)
        } catch (ex: JWTVerificationException) {
            null
        }

    private fun getNewAccessTokenExpiry(): Instant {
        val expirationMillis = when {
            developmentMode -> 30 * 24 * 60 * 60 * 1000L // 30 days
            else -> 60 * 60 * 1000L // 1 hour in prod
        }
        return Instant.ofEpochMilli(System.currentTimeMillis() + expirationMillis)
    }

}
