package com.tometrics.api.services.socialfeed.service

import com.tometrics.api.auth.domain.models.Requester
import com.tometrics.api.common.domain.models.ClimateZone
import com.tometrics.api.services.commongrpc.services.UserGrpcClient
import com.tometrics.api.services.socialfeed.db.LocationInfoDao
import com.tometrics.api.services.socialfeed.db.PostDao
import com.tometrics.api.services.socialfeed.db.PostReactionDao
import com.tometrics.api.services.socialfeed.db.UserDao
import com.tometrics.api.services.socialfeed.db.models.LocationInfoEntity
import com.tometrics.api.services.socialfeed.db.models.PostEntity
import com.tometrics.api.services.socialfeed.db.models.PostReactionEntity
import com.tometrics.api.services.socialfeed.db.models.UserEntity
import com.tometrics.api.services.socialfeed.domain.models.Reaction
import io.ktor.util.logging.*
import io.mockk.*
import kotlinx.coroutines.test.runTest
import java.time.Instant
import kotlin.test.*

class PostServiceTest {
    
    private val logger: Logger = mockk()
    private val userGrpcClient: UserGrpcClient = mockk()
    private val postDao: PostDao = mockk()
    private val userDao: UserDao = mockk {
        coEvery { getAllByIds(emptySet()) }.returns(emptyList())
    }
    private val locationInfoDao: LocationInfoDao = mockk {
        coEvery { getAllByIds(emptySet()) }.returns(emptyList())
    }
    private val postReactionDao: PostReactionDao = mockk {
        coEvery { getAllByPostIdsAndUserId(emptySet(), any()) }.returns(emptyList())
        coEvery { getLatestDistinctByPostId(emptySet()) }.returns(emptyList())
    }
    
    private val postService: PostService = DefaultPostService(
        logger = logger,
        userGrpcClient = userGrpcClient,
        postDao = postDao,
        userDao = userDao,
        locationInfoDao = locationInfoDao,
        postReactionDao = postReactionDao,
    )
    
    @Test
    fun `test getFeed with posts`() = runTest {
        val now = Instant.now()
        val userId1 = 1
        val userId2 = 2
        val postId1 = 1
        val postId2 = 2
        val locationId1 = 1
        
        val user1 = UserEntity(
            id = userId1,
            name = "User 1",
            climateZone = ClimateZone.TEMPERATE,
            createdAt = now.minusSeconds(3600),
            updatedAt = now.minusSeconds(3600),
        )
        
        val user2 = UserEntity(
            id = userId2,
            name = "User 2",
            climateZone = ClimateZone.TROPICAL,
            createdAt = now.minusSeconds(3600),
            updatedAt = now.minusSeconds(3600),
        )
        
        val location1 = LocationInfoEntity(
            locationId = locationId1,
            city = "City 1",
            country = "Country 1",
            countryCode = "C1",
            createdAt = now.minusSeconds(3600),
            updatedAt = now.minusSeconds(3600),
        )
        
        val post1 = PostEntity(
            id = postId1,
            userId = userId1,
            locationId = locationId1,
            images = listOf("image1.jpg"),
            text = "Post 1 text",
            reactionsCount = 1,
            commentsCount = 0,
            createdAt = now.minusSeconds(1800),
            updatedAt = now.minusSeconds(1800),
        )
        
        val post2 = PostEntity(
            id = postId2,
            userId = userId2,
            locationId = null,
            images = listOf("image2.jpg"),
            text = "Post 2 text",
            reactionsCount = 0,
            commentsCount = 0,
            createdAt = now.minusSeconds(900),
            updatedAt = now.minusSeconds(900),
        )
        
        val reaction1 = PostReactionEntity(
            postId = postId1,
            userId = userId2,
            reaction = Reaction.THUMBS_UP,
            createdAt = now.minusSeconds(600),
            updatedAt = now.minusSeconds(600),
        )
        
        every { logger.error(any<String>()) } just Runs
        
        coEvery { postDao.getFeed(any(), any()) } returns listOf(post1, post2)
        coEvery { userDao.getAllByIds(setOf(userId1, userId2)) } returns listOf(user1, user2)
        coEvery { locationInfoDao.getAllByIds(setOf(locationId1)) } returns listOf(location1)
        coEvery { postReactionDao.getAllByPostIdsAndUserId(setOf(postId1, postId2), userId2) } returns listOf(reaction1)
        coEvery { postReactionDao.getLatestDistinctByPostId(setOf(postId1, postId2)) } returns listOf(reaction1)
        
        val requester = Requester(userId2)
        val result = postService.getFeed(requester, now.toEpochMilli())
        
        assertEquals(2, result.size)
        
        val resultPost1 = result.find { it.createdAt == post1.createdAt.toEpochMilli() }
        assertNotNull(resultPost1)
        assertEquals(user1.name, resultPost1.user.name)
        assertEquals(location1.city, resultPost1.locationInfoDto?.city)
        assertEquals(post1.text, resultPost1.text)
        assertEquals(post1.images, resultPost1.images)
        assertEquals(Reaction.THUMBS_UP, resultPost1.myReaction)
        assertEquals(1, resultPost1.topReactions.size)
        assertEquals(Reaction.THUMBS_UP, resultPost1.topReactions[0])
        
        val resultPost2 = result.find { it.createdAt == post2.createdAt.toEpochMilli() }
        assertNotNull(resultPost2)
        assertEquals(user2.name, resultPost2.user.name)
        assertNull(resultPost2.locationInfoDto)
        assertEquals(post2.text, resultPost2.text)
        assertEquals(post2.images, resultPost2.images)
        assertNull(resultPost2.myReaction)
        assertTrue(resultPost2.topReactions.isEmpty())
        
        coVerify { postDao.getFeed(any(), any()) }
        coVerify { userDao.getAllByIds(setOf(userId1, userId2)) }
        coVerify { locationInfoDao.getAllByIds(setOf(locationId1)) }
        coVerify { postReactionDao.getAllByPostIdsAndUserId(setOf(postId1, postId2), userId2) }
        coVerify { postReactionDao.getLatestDistinctByPostId(setOf(postId1, postId2)) }
    }
    
    @Test
    fun `test getFeed with empty feed`() = runTest {
        val now = Instant.now()
        
        coEvery { postDao.getFeed(any(), any()) } returns emptyList()
        
        val requester = Requester(1)
        val result = postService.getFeed(requester, now.toEpochMilli())
        
        assertTrue(result.isEmpty())
        
        coVerify { postDao.getFeed(any(), any()) }
    }
    
    @Test
    fun `test getFeed with missing user`() = runTest {
        val now = Instant.now()
        val userId1 = 1
        val postId1 = 1
        
        val post1 = PostEntity(
            id = postId1,
            userId = userId1,
            locationId = null,
            images = listOf("image1.jpg"),
            text = "Post 1 text",
            reactionsCount = 0,
            commentsCount = 0,
            createdAt = now.minusSeconds(1800),
            updatedAt = now.minusSeconds(1800),
        )
        
        every { logger.error(any<String>()) } just Runs
        
        coEvery { postDao.getFeed(any(), any()) } returns listOf(post1)
        coEvery { userDao.getAllByIds(setOf(userId1)) } returns emptyList()
        coEvery { postReactionDao.getAllByPostIdsAndUserId(setOf(postId1), 2) } returns emptyList()
        coEvery { postReactionDao.getLatestDistinctByPostId(setOf(postId1)) } returns emptyList()

        val requester = Requester(2)
        val result = postService.getFeed(requester, now.toEpochMilli())
        
        assertTrue(result.isEmpty())
        
        coVerify { postDao.getFeed(any(), any()) }
        coVerify { userDao.getAllByIds(setOf(userId1)) }
        verify { logger.error(any<String>()) }
    }
    
    @Test
    fun `test getFeed with missing location`() = runTest {
        val now = Instant.now()
        val userId1 = 1
        val postId1 = 1
        val locationId1 = 1
        
        val user1 = UserEntity(
            id = userId1,
            name = "User 1",
            climateZone = ClimateZone.TEMPERATE,
            createdAt = now.minusSeconds(3600),
            updatedAt = now.minusSeconds(3600),
        )
        
        val post1 = PostEntity(
            id = postId1,
            userId = userId1,
            locationId = locationId1, // This location doesn't exist
            images = listOf("image1.jpg"),
            text = "Post 1 text",
            reactionsCount = 0,
            commentsCount = 0,
            createdAt = now.minusSeconds(1800),
            updatedAt = now.minusSeconds(1800),
        )
        
        every { logger.error(any<String>()) } just Runs
        
        coEvery { postDao.getFeed(any(), any()) } returns listOf(post1)
        coEvery { userDao.getAllByIds(setOf(userId1)) } returns listOf(user1)
        coEvery { locationInfoDao.getAllByIds(setOf(locationId1)) } returns emptyList()
        coEvery { postReactionDao.getAllByPostIdsAndUserId(setOf(postId1), any()) } returns emptyList()
        coEvery { postReactionDao.getLatestDistinctByPostId(setOf(postId1)) } returns emptyList()
        
        val requester = Requester(2)
        val result = postService.getFeed(requester, now.toEpochMilli())
        
        assertEquals(1, result.size)
        assertNull(result[0].locationInfoDto)
        
        coVerify { postDao.getFeed(any(), any()) }
        coVerify { userDao.getAllByIds(setOf(userId1)) }
        coVerify { locationInfoDao.getAllByIds(setOf(locationId1)) }
        verify { logger.error(any<String>()) }
    }

}