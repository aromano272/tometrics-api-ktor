package com.sproutscout.api.service

import com.sproutscout.api.database.RefreshTokenDao
import com.sproutscout.api.database.UserDao
import com.sproutscout.api.database.models.toDomain
import com.sproutscout.api.domain.models.*
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.*

interface AuthService {
    suspend fun registerAnon(): Tokens
    suspend fun login(user: User): Tokens
    suspend fun loginWithGoogle(requester: Requester?, idToken: String): Tokens
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

    override suspend fun loginWithGoogle(requester: Requester?, idToken: String): Tokens {
        val payload = googleAuthService.verify(idToken)
        val requesterUser = requester?.userId?.let {
            userDao.findById(it) ?: throw NotFoundException("User id not found")
        }?.toDomain()

        val googleUser = userDao.findByGoogleEmail(payload.email)?.toDomain()

        // requesterUser == null
        //   - existing gmail account != null
        //     - login
        //   - else
        //     - register
        // requester != null
        //  - anon == false
        //    - googleUser != null // most likely edge case just login
        //      - login
        //    - googleUser == null
        //      - TODO probably add new idp to the user?, not support currently, throws
        //  - anon == true
        //    - googleUser != null
        //      - TODO merge accs? Or fail? Or have confirmation endpoint?
        //      - login
        //    - else
        //      - update anon account to idpGoogle

        return if (requesterUser == null) {
            if (googleUser != null) {
                login(googleUser)
            } else {
                val user = registerUsingGoogle(payload)
                login(user)
            }
        } else {
            if (!requesterUser.anon) {
                // most likely edge case just login
                if (googleUser != null) {
                    login(googleUser)
                } else {
                    // Explanation: If requester user is not anonymous that means it already has a provider registered
                    // But we couldn't find a matching google account, so it either has another provider or has a different
                    // gmail one, if it has another provider we should add the google provider to it, if it already has a
                    // google provider but the emails dont match, than we should just throw
                    if (requesterUser.idpGoogleEmail != null) {
                        throw ConflictException("You already have a different google email assigned")
                    } else {
                        throw IllegalStateException("There are currently no other idp's so it's impossible for the user " +
                                "to not be anon but not have a different google idp attached, in the future when there " +
                                "more idp's this case will be reachable when, for eg., a user with facebook idp logs in with " +
                                "google we should add the google idp to this user so it has 2 idp's")
                    }
                }
            } else {
                if (googleUser != null) {
                    //      - TODO merge accs? Or fail? Or have confirmation endpoint?
                    //      - login
                    throw BadRequestException("Not supported yet")
                } else {
                    userDao.updateAnon(requesterUser.id, payload.email, false)
                    val newUser = userDao.findById(requesterUser.id)!!.toDomain()
                    login(newUser)
                }
            }
        }
    }

    private suspend fun registerUsingGoogle(payload: IdProviderPayload): User {
        val userId = userDao.insert(
            name = payload.name.orEmpty(),
            email = payload.email,
            idpGoogleEmail = payload.email,
            anon = false,
        )
        return userDao.findById(userId)!!.toDomain()
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

