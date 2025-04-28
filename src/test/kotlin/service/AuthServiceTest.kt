package com.sproutscout.api.service

import com.sproutscout.api.database.RefreshTokenDao
import com.sproutscout.api.database.UserDao
import com.sproutscout.api.database.models.RefreshTokenEntity
import com.sproutscout.api.database.models.UserEntity
import com.sproutscout.api.domain.models.UnauthorizedException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class AuthServiceTest {
    private val jwtService: JwtService = mockk()
    private val userDao: UserDao = mockk()
    private val refreshTokenDao: RefreshTokenDao = mockk()
    private val googleAuthService: GoogleAuthService = mockk()
    private val authService = DefaultAuthService(
        jwtService = jwtService,
        userDao = userDao,
        refreshTokenDao = refreshTokenDao,
        googleAuthService = googleAuthService,
    )

    @Test
    fun `refreshToken should throw UnauthorizedException when token not found`() = runTest {
        val refreshToken = "invalid_token"

        coEvery { refreshTokenDao.findByToken(refreshToken) } returns null

        assertFailsWith<UnauthorizedException> {
            authService.refreshToken(refreshToken)
        }
    }

    @Test
    fun `refreshToken should throw UnauthorizedException when token is expired`() = runTest {
        val refreshToken = "expired_token"
        val expiredToken = RefreshTokenEntity(
            id = 1,
            userId = 1,
            token = refreshToken,
            expiresAt = Instant.now().minusSeconds(3600),
            createdAt = Instant.now().minusSeconds(3600),
        )

        coEvery { refreshTokenDao.findByToken(refreshToken) } returns expiredToken
        coEvery { userDao.findById(expiredToken.userId) } returns TEST_USER_ENTITY

        assertFailsWith<UnauthorizedException> {
            authService.refreshToken(refreshToken)
        }
    }

    @Test
    fun `refreshToken should return new tokens when token is valid`() = runTest {
        val refreshToken = "valid_token"
        val user = TEST_USER_ENTITY
        val validToken = RefreshTokenEntity(
            id = user.id,
            userId = user.id,
            token = refreshToken,
            expiresAt = Instant.now().plusSeconds(3600),
            createdAt = Instant.now().plusSeconds(3600),
        )
        val newAccessToken = "new_access_token"

        coEvery { refreshTokenDao.findByToken(refreshToken) } returns validToken
        coEvery { userDao.findById(user.id) } returns user
        coEvery { refreshTokenDao.delete(refreshToken) } returns Unit
        coEvery { refreshTokenDao.insert(user.id, any(), any()) } returns Unit
        coEvery { jwtService.create(user.id, user.name) } returns newAccessToken

        val tokens = authService.refreshToken(refreshToken)

        assertNotNull(tokens)
        assertEquals(newAccessToken, tokens.access)
        assertNotNull(tokens.refresh)
    }

    @Test
    fun `logout should throw UnauthorizedException when token not found`() = runTest {
        val username = "testuser"
        val refreshToken = "invalid_token"

        coEvery { refreshTokenDao.findByToken(refreshToken) } returns null

        assertFailsWith<UnauthorizedException> {
            authService.logout(username, refreshToken)
        }
    }

    @Test
    fun `logout should throw UnauthorizedException when username doesn't match`() = runTest {
        val username = "wrong_user"
        val refreshToken = "valid_token"
        val tokenEntity = RefreshTokenEntity(
            id = 1,
            userId = 1,
            token = refreshToken,
            expiresAt = Instant.now().plusSeconds(3600),
            createdAt = Instant.now().plusSeconds(3600),
        )
        val user = TEST_USER_ENTITY.copy(name = "correct_user")

        coEvery { refreshTokenDao.findByToken(refreshToken) } returns tokenEntity
        coEvery { userDao.findById(tokenEntity.userId) } returns user

        assertFailsWith<UnauthorizedException> {
            authService.logout(username, refreshToken)
        }
    }

    @Test
    fun `logout should delete token when successful`() = runTest {
        val username = "testuser"
        val refreshToken = "valid_token"
        val tokenEntity = RefreshTokenEntity(
            id = 1,
            userId = 1,
            token = refreshToken,
            expiresAt = Instant.now().plusSeconds(3600),
            createdAt = Instant.now().plusSeconds(3600),
        )
        val user = TEST_USER_ENTITY.copy(name = username)

        coEvery { refreshTokenDao.findByToken(refreshToken) } returns tokenEntity
        coEvery { userDao.findById(tokenEntity.userId) } returns user
        coEvery { refreshTokenDao.delete(refreshToken) } returns Unit

        authService.logout(username, refreshToken)

        coVerify { refreshTokenDao.delete(refreshToken) }
    }

    companion object {
        private val TEST_USER_ENTITY = UserEntity(
            id = 1,
            name = "testuser",
            email = "test@example.com",
            idpGoogleEmail = "test@gmail.com",
            anon = false,
        )
    }
} 