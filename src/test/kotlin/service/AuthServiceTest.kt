package com.tometrics.api.service

import com.tometrics.api.auth.domain.models.Requester
import com.tometrics.api.db.RefreshTokenDao
import com.tometrics.api.db.UserDao
import com.tometrics.api.db.models.RefreshTokenEntity
import com.tometrics.api.db.models.UserEntity
import com.tometrics.api.domain.models.*
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
    fun `loginWithGoogle with no requester and existing Google user should login that user`() = runTest {
        val idToken = "valid_token"
        val payload = IdProviderPayload.Google(email = "test@gmail.com", name = "Test User")
        val existingUser = TEST_USER_ENTITY
        val tokens = MOCK_TOKENS

        coEvery { googleAuthService.verify(idToken) } returns payload
        coEvery { userDao.findByGoogleEmail(payload.email) } returns existingUser
        coEvery { jwtService.create(existingUser.id, false) } returns tokens.access
        coEvery { refreshTokenDao.insert(existingUser.id, any(), any()) } returns Unit

        val result = authService.loginWithGoogle(null, idToken)

        assertEquals(tokens.access, result.access)
        coVerify { userDao.findByGoogleEmail(payload.email) }
    }

    @Test
    fun `loginWithGoogle with no requester and no existing user should register new user`() = runTest {
        val idToken = "valid_token"
        val payload = IdProviderPayload.Google(email = "new@gmail.com", name = "New User")
        val newUserId = 2
        val newUser = TEST_USER_ENTITY.copy(id = newUserId, idpGoogleEmail = payload.email)
        val tokens = MOCK_TOKENS

        coEvery { googleAuthService.verify(idToken) } returns payload
        coEvery { userDao.findByGoogleEmail(payload.email) } returns null
        coEvery {
            userDao.insert(
                name = payload.name.orEmpty(),
                idpGoogleEmail = payload.email,
                anon = false,
            )
        } returns newUserId
        coEvery { userDao.findById(newUserId) } returns newUser
        coEvery { jwtService.create(newUserId, false) } returns tokens.access
        coEvery { refreshTokenDao.insert(newUserId, any(), any()) } returns Unit

        val result = authService.loginWithGoogle(null, idToken)

        assertEquals(tokens.access, result.access)
        coVerify {
            userDao.insert(
                name = payload.name.orEmpty(),
                idpGoogleEmail = payload.email,
                anon = false
            )
        }
    }

    @Test
    fun `loginWithGoogle with non-anonymous requester that already has an idpGoogleEmail and different from the payload should throw ConflictException`() =
        runTest {
            val idToken = "valid_token"
            val payload = IdProviderPayload.Google(email = "different@gmail.com", name = "Different User")
            val requester = Requester(TEST_USER_ENTITY.id)
            val existingUser = TEST_USER_ENTITY.copy(idpGoogleEmail = "existing@gmail.com", anon = false)

            coEvery { googleAuthService.verify(idToken) } returns payload
            coEvery { userDao.findById(requester.userId) } returns existingUser
            coEvery { userDao.findByGoogleEmail("different@gmail.com") } returns null

            assertFailsWith<ConflictException> {
                authService.loginWithGoogle(requester, idToken)
            }
        }

    @Test
    fun `loginWithGoogle with non-anonymous requester and google user already exists and is the same as the requester then log into googleUser`() =
        runTest {
            val idToken = "valid_token"
            val payload = IdProviderPayload.Google(email = "different@gmail.com", name = "Different User")
            val requester = Requester(TEST_USER_ENTITY.id)
            val requesterUser = TEST_USER_ENTITY.copy(idpGoogleEmail = "existing@gmail.com", anon = false)
            val tokens = MOCK_TOKENS

            coEvery { googleAuthService.verify(idToken) } returns payload
            coEvery { userDao.findById(requester.userId) } returns requesterUser
            coEvery { userDao.findByGoogleEmail("different@gmail.com") } returns requesterUser
            coEvery { jwtService.create(requesterUser.id, false) } returns tokens.access
            coEvery { refreshTokenDao.insert(requesterUser.id, any(), any()) } returns Unit

            val result = authService.loginWithGoogle(requester, idToken)
            assertEquals(tokens.access, result.access)
        }

    @Test
    fun `loginWithGoogle with non-anonymous requester and google user already exists and is different than the requester then throw ConflictException`() =
        runTest {
            val idToken = "valid_token"
            val payload = IdProviderPayload.Google(email = "different@gmail.com", name = "Different User")
            val requester = Requester(TEST_USER_ENTITY.id)
            val existingUser = TEST_USER_ENTITY.copy(idpGoogleEmail = "existing@gmail.com", anon = false)
            val differentUser = UserEntity(
                id = 123,
                name = "Different User",
                idpGoogleEmail = "different@gmail.com",
                idpFacebookId = null,
                idpFacebookEmail = null,
                anon = false,
            )
            val tokens = MOCK_TOKENS

            coEvery { googleAuthService.verify(idToken) } returns payload
            coEvery { userDao.findById(requester.userId) } returns existingUser
            coEvery { userDao.findByGoogleEmail("different@gmail.com") } returns differentUser
            coEvery { jwtService.create(differentUser.id, false) } returns tokens.access
            coEvery { refreshTokenDao.insert(differentUser.id, any(), any()) } returns Unit

            assertFailsWith<ConflictException> {
                authService.loginWithGoogle(requester, idToken)
            }
        }

    @Test
    fun `loginWithGoogle with anonymous requester and existing Google user should throw BadRequestException`() =
        runTest {
            val idToken = "valid_token"
            val payload = IdProviderPayload.Google(email = "existing@gmail.com", name = "Existing User")
            val requester = Requester(TEST_USER_ENTITY.id)
            val anonUser = TEST_USER_ENTITY.copy(idpGoogleEmail = null, anon = true)
            val existingGoogleUser = TEST_USER_ENTITY.copy(idpGoogleEmail = payload.email)

            coEvery { googleAuthService.verify(idToken) } returns payload
            coEvery { userDao.findById(requester.userId) } returns anonUser
            coEvery { userDao.findByGoogleEmail(payload.email) } returns existingGoogleUser

            assertFailsWith<BadRequestException> {
                authService.loginWithGoogle(requester, idToken)
            }
        }

    @Test
    fun `loginWithGoogle with anonymous requester and no existing Google user should update anonymous user`() =
        runTest {
            val idToken = "valid_token"
            val payload = IdProviderPayload.Google(email = "new@gmail.com", name = "New User")
            val requester = Requester(TEST_USER_ENTITY.id)
            val anonUser = TEST_USER_ENTITY.copy(idpGoogleEmail = null, anon = true)
            val updatedUser = anonUser.copy(idpGoogleEmail = payload.email, anon = false)
            val tokens = MOCK_TOKENS

            coEvery { googleAuthService.verify(idToken) } returns payload
            coEvery { userDao.findById(requester.userId) }.returnsMany(anonUser, updatedUser)
            coEvery { userDao.findByGoogleEmail(payload.email) } returns null
            coEvery {
                userDao.updateAnon(
                    id = anonUser.id,
                    idpGoogleEmail = payload.email,
                    anon = false,
                )
            } returns 1
            coEvery { jwtService.create(updatedUser.id, false) } returns tokens.access
            coEvery { refreshTokenDao.insert(updatedUser.id, any(), any()) } returns Unit

            val result = authService.loginWithGoogle(requester, idToken)

            assertEquals(tokens.access, result.access)
            coVerify {
                userDao.updateAnon(
                    id = anonUser.id,
                    idpGoogleEmail = payload.email,
                    anon = false,
                )
            }
        }

    @Test
    fun `loginWithFacebook with no requester and existing Facebook user should login that user`() = runTest {
        val payload = IdProviderPayload.Facebook(id = "facebookid", name = "Test User", email = "test@facebook.com")
        val existingUser = TEST_USER_ENTITY
        val tokens = MOCK_TOKENS

        coEvery { userDao.findByFacebookId(payload.id) } returns existingUser
        coEvery { jwtService.create(existingUser.id, false) } returns tokens.access
        coEvery { refreshTokenDao.insert(existingUser.id, any(), any()) } returns Unit

        val result = authService.loginWithFacebook(null, payload)

        assertEquals(tokens.access, result.access)
        coVerify { userDao.findByFacebookId(payload.id) }
    }

    @Test
    fun `loginWithFacebook with no requester and no existing user should register new user`() = runTest {
        val payload = IdProviderPayload.Facebook(id = "newfacebookid", name = "New User", email = "new@facebook.com")
        val newUserId = 2
        val newUser = TEST_USER_ENTITY.copy(
            id = newUserId,
            idpFacebookId = payload.id,
            idpFacebookEmail = payload.email
        )
        val tokens = MOCK_TOKENS

        coEvery { userDao.findByFacebookId(payload.id) } returns null
        coEvery {
            userDao.insert(
                name = payload.name,
                idpFacebookId = payload.id,
                idpFacebookEmail = payload.email,
                anon = false,
            )
        } returns newUserId
        coEvery { userDao.findById(newUserId) } returns newUser
        coEvery { jwtService.create(newUserId, false) } returns tokens.access
        coEvery { refreshTokenDao.insert(newUserId, any(), any()) } returns Unit

        val result = authService.loginWithFacebook(null, payload)

        assertEquals(tokens.access, result.access)
        coVerify {
            userDao.insert(
                name = payload.name,
                idpFacebookId = payload.id,
                idpFacebookEmail = payload.email,
                anon = false
            )
        }
    }

    @Test
    fun `loginWithFacebook with non-anonymous requester that already has an idpFacebookId and different from the payload should throw ConflictException`() =
        runTest {
            val payload = IdProviderPayload.Facebook(
                id = "differentid",
                name = "Different User",
                email = "different@facebook.com"
            )
            val requester = Requester(TEST_USER_ENTITY.id)
            val existingUser = TEST_USER_ENTITY.copy(idpFacebookId = "existingid", anon = false)

            coEvery { userDao.findById(requester.userId) } returns existingUser
            coEvery { userDao.findByFacebookId("differentid") } returns null

            assertFailsWith<ConflictException> {
                authService.loginWithFacebook(requester, payload)
            }
        }

    @Test
    fun `loginWithFacebook with non-anonymous requester and facebook user already exists and is the same as the requester then log into facebookUser`() =
        runTest {
            val payload = IdProviderPayload.Facebook(id = "facebookid", name = "Test User", email = "test@facebook.com")
            val requester = Requester(TEST_USER_ENTITY.id)
            val requesterUser = TEST_USER_ENTITY.copy(idpFacebookId = "facebookid", anon = false)
            val tokens = MOCK_TOKENS

            coEvery { userDao.findById(requester.userId) } returns requesterUser
            coEvery { userDao.findByFacebookId("facebookid") } returns requesterUser
            coEvery { jwtService.create(requesterUser.id, false) } returns tokens.access
            coEvery { refreshTokenDao.insert(requesterUser.id, any(), any()) } returns Unit

            val result = authService.loginWithFacebook(requester, payload)
            assertEquals(tokens.access, result.access)
        }

    @Test
    fun `loginWithFacebook with non-anonymous requester and facebook user already exists and is different than the requester then throw ConflictException`() =
        runTest {
            val payload = IdProviderPayload.Facebook(
                id = "differentid",
                name = "Different User",
                email = "different@facebook.com"
            )
            val requester = Requester(TEST_USER_ENTITY.id)
            val existingUser = TEST_USER_ENTITY.copy(idpFacebookId = "existingid", anon = false)
            val differentUser = UserEntity(
                id = 123,
                name = "Different User",
                idpGoogleEmail = null,
                idpFacebookId = "differentid",
                idpFacebookEmail = "different@facebook.com",
                anon = false,
            )

            coEvery { userDao.findById(requester.userId) } returns existingUser
            coEvery { userDao.findByFacebookId("differentid") } returns differentUser

            assertFailsWith<ConflictException> {
                authService.loginWithFacebook(requester, payload)
            }
        }

    @Test
    fun `loginWithFacebook with anonymous requester and existing Facebook user should throw BadRequestException`() =
        runTest {
            val payload =
                IdProviderPayload.Facebook(id = "existingid", name = "Existing User", email = "existing@facebook.com")
            val requester = Requester(TEST_USER_ENTITY.id)
            val anonUser = TEST_USER_ENTITY.copy(idpFacebookId = null, anon = true)
            val existingFacebookUser = TEST_USER_ENTITY.copy(idpFacebookId = payload.id)

            coEvery { userDao.findById(requester.userId) } returns anonUser
            coEvery { userDao.findByFacebookId(payload.id) } returns existingFacebookUser

            assertFailsWith<BadRequestException> {
                authService.loginWithFacebook(requester, payload)
            }
        }

    @Test
    fun `loginWithFacebook with anonymous requester and no existing Facebook user should update anonymous user`() =
        runTest {
            val payload = IdProviderPayload.Facebook(id = "newid", name = "New User", email = "new@facebook.com")
            val requester = Requester(TEST_USER_ENTITY.id)
            val anonUser = TEST_USER_ENTITY.copy(idpFacebookId = null, anon = true)
            val updatedUser = anonUser.copy(idpFacebookId = payload.id, idpFacebookEmail = payload.email, anon = false)
            val tokens = MOCK_TOKENS

            coEvery { userDao.findById(requester.userId) }.returnsMany(anonUser, updatedUser)
            coEvery { userDao.findByFacebookId(payload.id) } returns null
            coEvery {
                userDao.updateAnon(
                    id = anonUser.id,
                    idpFacebookId = payload.id,
                    idpFacebookEmail = payload.email,
                    anon = false,
                )
            } returns 1
            coEvery { jwtService.create(updatedUser.id, false) } returns tokens.access
            coEvery { refreshTokenDao.insert(updatedUser.id, any(), any()) } returns Unit

            val result = authService.loginWithFacebook(requester, payload)

            assertEquals(tokens.access, result.access)
            coVerify {
                userDao.updateAnon(
                    id = anonUser.id,
                    idpFacebookId = payload.id,
                    idpFacebookEmail = payload.email,
                    anon = false,
                )
            }
        }

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
        coEvery { jwtService.create(user.id, false) } returns newAccessToken

        val tokens = authService.refreshToken(refreshToken)

        assertNotNull(tokens)
        assertEquals(newAccessToken, tokens.access)
        assertNotNull(tokens.refresh)
    }

    @Test
    fun `logout should throw UnauthorizedException when token not found`() = runTest {
        val requester = Requester(TEST_USER_ENTITY.id)
        val refreshToken = "invalid_token"

        coEvery { refreshTokenDao.findByToken(refreshToken) } returns null

        assertFailsWith<UnauthorizedException> {
            authService.logout(requester, refreshToken)
        }
    }

    @Test
    fun `logout should delete token when successful`() = runTest {
        val requester = Requester(TEST_USER_ENTITY.id)
        val refreshToken = "valid_token"
        val tokenEntity = RefreshTokenEntity(
            id = 1,
            userId = 1,
            token = refreshToken,
            expiresAt = Instant.now().plusSeconds(3600),
            createdAt = Instant.now().plusSeconds(3600),
        )

        coEvery { refreshTokenDao.findByToken(refreshToken) } returns tokenEntity
        coEvery { userDao.findById(tokenEntity.userId) } returns TEST_USER_ENTITY
        coEvery { refreshTokenDao.delete(refreshToken) } returns Unit

        authService.logout(requester, refreshToken)

        coVerify { refreshTokenDao.delete(refreshToken) }
    }

    companion object {
        private val TEST_USER_ENTITY = UserEntity(
            id = 1,
            name = "testuser",
            idpGoogleEmail = "test@gmail.com",
            idpFacebookId = "facebookid",
            idpFacebookEmail = "facebook@hotmail.com",
            anon = false,
        )

        private val MOCK_TOKENS = Tokens(access = "mock_access_token", refresh = "mock_refresh_token")
    }
} 