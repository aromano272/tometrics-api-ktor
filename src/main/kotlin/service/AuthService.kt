package com.sproutscout.api.service

import com.sproutscout.api.database.RefreshTokenDao
import com.sproutscout.api.database.UserDao
import com.sproutscout.api.models.ConflictException
import com.sproutscout.api.models.NotFoundException
import com.sproutscout.api.models.Tokens
import com.sproutscout.api.models.UnauthorizedException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.server.auth.OAuthAccessTokenResponse
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.*

interface AuthService {
    suspend fun registerAndLogin(
        username: String,
        email: String,
        password: String,
        isAdmin: Boolean
    ): Tokens
    suspend fun login(username: String, password: String): Tokens
    suspend fun refreshToken(refreshToken: String): Tokens
    suspend fun logout(username: String, refreshToken: String)

    suspend fun handleGoogleCallback(principal: OAuthAccessTokenResponse.OAuth2)
}

class DefaultAuthService(
    private val httpClient: HttpClient,
    private val jwtService: JwtService,
    private val userDao: UserDao,
    private val refreshTokenDao: RefreshTokenDao,
) : AuthService {

    override suspend fun registerAndLogin(
        username: String,
        email: String,
        password: String,
        isAdmin: Boolean
    ): Tokens {
        if (userDao.findByUsername(username) != null) throw ConflictException("username already exists")

//        val hashedPassword = passwordService.hash(password)
        userDao.insert(username, email, isAdmin, "")

        return login(username, password)
    }

    override suspend fun login(username: String, password: String): Tokens {
        val user = userDao.findByUsername(username) ?: throw NotFoundException("couldn't find user")
        val storedPassHash = user.passwordHash
//        val isValid = passwordService.verify(password, storedPassHash)

        return if (true) {
            val access = jwtService.create(user.id, username)
            val refresh = UUID.randomUUID().toString()

            val expiry = getNewRefreshTokenExpiry()
            refreshTokenDao.insert(user.id, refresh, expiry)

            Tokens(access, refresh)
        } else {
            throw UnauthorizedException("wrong password")
        }
    }

    override suspend fun refreshToken(refreshToken: String): Tokens {
        val stored = refreshTokenDao.findByToken(refreshToken)
            ?: throw UnauthorizedException("couldn't find refresh token")
        val user = userDao.findById(stored.userId) ?: throw NotFoundException("couldn't find user")

        if (stored.expiresAt.isBefore(Instant.now())) throw UnauthorizedException("refresh token has expired")

        refreshTokenDao.delete(refreshToken)

        val newRefreshToken = UUID.randomUUID().toString()
        val expiry = getNewRefreshTokenExpiry()
        refreshTokenDao.insert(user.id, newRefreshToken, expiry)

        val newAccessToken = jwtService.create(user.id, user.username)

        return Tokens(newAccessToken, newRefreshToken)
    }

    override suspend fun logout(username: String, refreshToken: String) {
        val stored =
            refreshTokenDao.findByToken(refreshToken) ?: throw UnauthorizedException("couldn't find refresh token")

        val user = userDao.findById(stored.userId) ?: throw NotFoundException("couldn't find user")
        if (user.username != username) throw UnauthorizedException("username doesn't match refreshToken's username")

        refreshTokenDao.delete(refreshToken)
    }

    override suspend fun handleGoogleCallback(principal: OAuthAccessTokenResponse.OAuth2) {
        val userInfo: GoogleUserInfo = httpClient.get("https://www.googleapis.com/oauth2/v2/userinfo") {
            bearerAuth(principal.accessToken)
        }.body()


    }

    private fun getNewRefreshTokenExpiry() = Instant.ofEpochMilli(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)
}

@Serializable
data class GoogleUserInfo(
    val id: String,
    val email: String,
    val verified_email: Boolean,
    val name: String,
    val picture: String,
    val locale: String
)

