package service

import com.tometrics.api.auth.domain.models.Requester
import com.tometrics.api.services.commongrpc.models.socialgraph.GrpcSocialConnections
import com.tometrics.api.services.commongrpc.services.SocialGraphGrpcClient
import com.tometrics.api.services.user.db.GeoNameCity500Dao
import com.tometrics.api.services.user.db.UserDao
import com.tometrics.api.services.user.db.models.UserEntity
import com.tometrics.api.services.user.db.models.toDomain
import com.tometrics.api.services.user.domain.models.User
import com.tometrics.api.services.user.domain.models.UserWithSocialConnections
import com.tometrics.api.services.user.services.DefaultUserService
import io.ktor.util.logging.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class UserServiceTest {

    private val logger: Logger = mockk()
    private val socialGraphGrpcClient: SocialGraphGrpcClient = mockk()
    private val userDao: UserDao = mockk()
    private val city500Dao: GeoNameCity500Dao = mockk()
    private val userService = DefaultUserService(
        logger,
        socialGraphGrpcClient,
        userDao,
        city500Dao,
    )

    @Test
    fun `given user exists when get profile then return it`() = runTest {
        val requester = Requester(1)

        val entity = UserEntity(
            id = 1,
            name = "Test User",
            idpGoogleEmail = null,
            idpFacebookId = null,
            idpFacebookEmail = null,
            anon = false,
            locationId = null,
            metricUnits = true,
            climateZone = null,
            updatedAt = Instant.now(),
        )
        val followersEntities = buildList {
            repeat(10) {
                add(entity.copy(id = 100 + it))
            }
        }
        val followers = followersEntities.map { it.toDomain(null) }
        val followersIds = followersEntities.map { it.id }

        val followingEntities = buildList {
            repeat(13) {
                add(entity.copy(id = 200 + it))
            }
        }
        val following = followingEntities.map { it.toDomain(null) }
        val followingIds = followingEntities.map { it.id }
        coEvery { socialGraphGrpcClient.grpcGetConnectionsByUserId(requester.userId) }
            .returns(GrpcSocialConnections(
                followers = followersIds,
                following = followingIds,
            ))
        coEvery { userDao.findById(1) }.returns(entity)
        coEvery { userDao.getAllByIds(followersIds.toSet()) }
            .returns(followersEntities)
        coEvery { userDao.getAllByIds(followingIds.toSet()) }
            .returns(followingEntities)

        val actual = userService.get(requester)

        assertEquals(
            UserWithSocialConnections(
                user = User(
                    id = 1,
                    name = "Test User",
                    idpGoogleEmail = null,
                    idpFacebookId = null,
                    idpFacebookEmail = null,
                    anon = false,
                    location = null,
                    metricUnits = true,
                    climateZone = null,
                    updatedAt = actual.user.updatedAt, // ignoring updatedAt checking
                ),
                followers = followers,
                following = following,
            ),
            actual,
        )
        coVerify { userDao.getAllByIds(followersIds.toSet()) }
        coVerify { userDao.getAllByIds(followingIds.toSet()) }
    }

    @Test
    fun `given user exists when update profile then return updated profile`() = runTest {
        val requester = Requester(1)

        val originalEntity = UserEntity(
            id = 1,
            name = "Test User",
            idpGoogleEmail = null,
            idpFacebookId = null,
            idpFacebookEmail = null,
            anon = false,
            locationId = null,
            metricUnits = true,
            climateZone = null,
            updatedAt = Instant.now(),
        )

        val updatedEntity = UserEntity(
            id = 1,
            name = "Updated Name",
            idpGoogleEmail = null,
            idpFacebookId = null,
            idpFacebookEmail = null,
            anon = false,
            locationId = null,
            metricUnits = false,
            climateZone = null,
            updatedAt = Instant.now(),
        )

        coEvery { userDao.update(1, "Updated Name", null, null, null, null, 123, false, null) }.returns(1)
        coEvery { userDao.findById(1) }.returns(updatedEntity)

        val actual = userService.update(
            requester,
            name = "Updated Name",
            locationId = 123,
            metricUnits = false,
            climateZone = null,
        )

        coVerify { userDao.update(1, "Updated Name", null, null, null, null, 123, false, null) }

        assertEquals(
            User(
                id = 1,
                name = "Updated Name",
                idpGoogleEmail = null,
                idpFacebookId = null,
                idpFacebookEmail = null,
                anon = false,
                location = null,
                metricUnits = false,
                climateZone = null,
                updatedAt = actual.updatedAt, // ignoring updatedAt checking
            ),
            actual,
        )
    }

}
