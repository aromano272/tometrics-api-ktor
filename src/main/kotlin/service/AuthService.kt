package com.sproutscout.api.service

import com.sproutscout.api.database.RefreshTokenDao
import com.sproutscout.api.database.UserDao
import com.sproutscout.api.database.models.toDomain
import com.sproutscout.api.models.IdProviderType
import com.sproutscout.api.models.NotFoundException
import com.sproutscout.api.models.Tokens
import com.sproutscout.api.models.UnauthorizedException
import com.sproutscout.api.models.User
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.*

interface AuthService {
    suspend fun registerAnon(): Tokens
    suspend fun login(user: User): Tokens
    suspend fun loginWithGoogle(credential: String): Tokens
    suspend fun refreshToken(refreshToken: String): Tokens
    suspend fun logout(username: String, refreshToken: String)
}

class DefaultAuthService(
    private val jwtService: JwtService,
    private val googleAuthService: GoogleAuthService,
    private val userDao: UserDao,
    private val refreshTokenDao: RefreshTokenDao,
) : AuthService {

    override suspend fun registerAnon(): Tokens {
        val userId = userDao.insert("", "", null, anon = true)
        val user = userDao.findById(userId)!!.toDomain()
        return login(user)
    }

    override suspend fun login(user: User): Tokens {
        val access = jwtService.create(user.id, user.email)
        val refresh = UUID.randomUUID().toString()

        val expiry = getNewRefreshTokenExpiry()
        refreshTokenDao.insert(user.id, refresh, expiry)

        return Tokens(access, refresh)
    }

    override suspend fun loginWithGoogle(credential: String): Tokens {
        val payload = googleAuthService.verify(credential)
        val user = userDao.findByEmailAndIdProviderType(payload.email, IdProviderType.GOOGLE)?.toDomain()

        return if (user != null) {
            userDao.updateAnon(user.id, IdProviderType.GOOGLE, false)
            login(user)
        } else {
            val userId = userDao.insert(
                name = payload.name.orEmpty(),
                email = payload.email,
                idProviderType = IdProviderType.GOOGLE,
                anon = false,
            )
            val user = userDao.findById(userId)!!.toDomain()
            login(user)
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

        val newAccessToken = jwtService.create(user.id, user.name)

        return Tokens(newAccessToken, newRefreshToken)
    }

    override suspend fun logout(username: String, refreshToken: String) {
        val stored =
            refreshTokenDao.findByToken(refreshToken) ?: throw UnauthorizedException("couldn't find refresh token")

        val user = userDao.findById(stored.userId) ?: throw NotFoundException("couldn't find user")
        if (user.name != username) throw UnauthorizedException("username doesn't match refreshToken's username")

        refreshTokenDao.delete(refreshToken)
    }

    private fun getNewRefreshTokenExpiry() = Instant.ofEpochMilli(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000)
}

@Serializable
data class GoogleUserInfo(
    val id: String?,
    val email: String?,
    val verified_email: Boolean?,
    val name: String?,
    val picture: String?,
    val locale: String?,
)

