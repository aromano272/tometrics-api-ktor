package com.tometrics.api.services.socialfeed.service

import com.tometrics.api.auth.domain.models.Requester
import com.tometrics.api.common.domain.models.ClimateZone
import com.tometrics.api.services.commongrpc.services.MediaGrpcClient
import com.tometrics.api.services.commongrpc.services.UserGrpcClient
import com.tometrics.api.services.socialfeed.db.CommentDao
import com.tometrics.api.services.socialfeed.db.CommentReactionDao
import com.tometrics.api.services.socialfeed.db.PostDao
import com.tometrics.api.services.socialfeed.db.UserDao
import com.tometrics.api.services.socialfeed.db.models.CommentEntity
import com.tometrics.api.services.socialfeed.db.models.CommentReactionEntity
import com.tometrics.api.services.socialfeed.db.models.UserEntity
import com.tometrics.api.services.socialfeed.domain.models.InvalidMediaUrls
import com.tometrics.api.services.socialfeed.domain.models.Reaction
import io.ktor.util.logging.*
import io.mockk.*
import kotlinx.coroutines.test.runTest
import java.time.Instant
import kotlin.test.*

class CommentServiceTest {

    private val logger: Logger = mockk()
    private val userGrpcClient: UserGrpcClient = mockk()
    private val mediaGrpcClient: MediaGrpcClient = mockk()
    private val userDao: UserDao = mockk {
        coEvery { getAllByIds(emptySet()) }.returns(emptyList())
    }
    private val postDao: PostDao = mockk()
    private val commentDao: CommentDao = mockk()
    private val commentReactionDao: CommentReactionDao = mockk {
        coEvery { getAllByCommentIdsAndUserId(emptySet(), any()) }.returns(emptyList())
        coEvery { getLatestDistinctByCommentId(emptySet()) }.returns(emptyList())
    }

    private val commentService: CommentService = DefaultCommentService(
        logger = logger,
        userGrpcClient = userGrpcClient,
        mediaGrpcClient = mediaGrpcClient,
        userDao = userDao,
        postDao = postDao,
        commentDao = commentDao,
        commentReactionDao = commentReactionDao,
    )

    @Test
    fun `test getAllByPostId with comments`() = runTest {
        val now = Instant.now()
        val userId1 = 1
        val userId2 = 2
        val postId1 = 1
        val commentId1 = 1
        val commentId2 = 2

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

        val comment1 = CommentEntity(
            id = commentId1,
            userId = userId1,
            parentId = null,
            text = "Comment 1 text",
            image = "image1.jpg",
            reactionsCount = 1,
            createdAt = now.minusSeconds(1800),
            updatedAt = now.minusSeconds(1800),
        )

        val comment2 = CommentEntity(
            id = commentId2,
            userId = userId2,
            parentId = commentId1,
            text = "Comment 2 text",
            image = null,
            reactionsCount = 0,
            createdAt = now.minusSeconds(900),
            updatedAt = now.minusSeconds(900),
        )

        val reaction1 = CommentReactionEntity(
            commentId = commentId1,
            userId = userId2,
            reaction = Reaction.THUMBS_UP,
            createdAt = now.minusSeconds(600),
            updatedAt = now.minusSeconds(600),
        )

        every { logger.error(any<String>()) } just Runs

        coEvery { commentDao.getAllByPostId(postId1, any(), any()) } returns listOf(comment1, comment2)
        coEvery { userDao.getAllByIds(setOf(userId1, userId2)) } returns listOf(user1, user2)
        coEvery { commentReactionDao.getAllByCommentIdsAndUserId(setOf(commentId1, commentId2), userId2) } returns listOf(reaction1)
        coEvery { commentReactionDao.getLatestDistinctByCommentId(setOf(commentId1, commentId2)) } returns listOf(reaction1)

        val requester = Requester(userId2)
        val result = commentService.getAllByPostId(requester, postId1, now.toEpochMilli())

        assertEquals(2, result.size)

        val resultComment1 = result.find { it.parentId == null }
        assertNotNull(resultComment1)
        assertEquals(user1.name, resultComment1.user.name)
        assertEquals(comment1.text, resultComment1.text)
        assertEquals(comment1.image, resultComment1.image)
        assertEquals(Reaction.THUMBS_UP, resultComment1.myReaction)
        assertEquals(1, resultComment1.reactions.size)
        assertEquals(Reaction.THUMBS_UP, resultComment1.reactions[0])

        val resultComment2 = result.find { it.parentId == commentId1 }
        assertNotNull(resultComment2)
        assertEquals(user2.name, resultComment2.user.name)
        assertEquals(comment2.text, resultComment2.text)
        assertEquals(comment2.image, resultComment2.image)
        assertNull(resultComment2.myReaction)
        assertTrue(resultComment2.reactions.isEmpty())

        coVerify { commentDao.getAllByPostId(postId1, any(), any()) }
        coVerify { userDao.getAllByIds(setOf(userId1, userId2)) }
        coVerify { commentReactionDao.getAllByCommentIdsAndUserId(setOf(commentId1, commentId2), userId2) }
        coVerify { commentReactionDao.getLatestDistinctByCommentId(setOf(commentId1, commentId2)) }
    }

    @Test
    fun `test getAllByPostId with empty comments`() = runTest {
        val now = Instant.now()
        val postId1 = 1

        coEvery { commentDao.getAllByPostId(postId1, any(), any()) } returns emptyList()

        val requester = Requester(1)
        val result = commentService.getAllByPostId(requester, postId1, now.toEpochMilli())

        assertTrue(result.isEmpty())

        coVerify { commentDao.getAllByPostId(postId1, any(), any()) }
    }

    @Test
    fun `test createComment success`() = runTest {
        val now = Instant.now()
        val userId1 = 1
        val postId1 = 1
        val commentId1 = 1
        val text = "New comment"
        val image = "image.jpg"

        val user1 = UserEntity(
            id = userId1,
            name = "User 1",
            climateZone = ClimateZone.TEMPERATE,
            createdAt = now.minusSeconds(3600),
            updatedAt = now.minusSeconds(3600),
        )

        val comment1 = CommentEntity(
            id = commentId1,
            userId = userId1,
            parentId = null,
            text = text,
            image = image,
            reactionsCount = 0,
            createdAt = now,
            updatedAt = now,
        )

        coEvery { userDao.findById(userId1) } returns user1
        coEvery { mediaGrpcClient.validateMediaUrl(userId1, image) } returns true
        coEvery { commentDao.insert(userId1, null, text, image) } returns commentId1
        coEvery { commentDao.findById(commentId1) } returns comment1
        coEvery { userDao.getAllByIds(setOf(userId1)) } returns listOf(user1)
        coEvery { commentReactionDao.getAllByCommentIdsAndUserId(setOf(commentId1), userId1) } returns emptyList()
        coEvery { commentReactionDao.getLatestDistinctByCommentId(setOf(commentId1)) } returns emptyList()

        val requester = Requester(userId1)
        val result = commentService.createComment(requester, postId1, null, text, image)

        assertEquals(text, result.text)
        assertEquals(image, result.image)
        assertEquals(user1.name, result.user.name)

        coVerify { userDao.findById(userId1) }
        coVerify { mediaGrpcClient.validateMediaUrl(userId1, image) }
        coVerify { commentDao.insert(userId1, null, text, image) }
        coVerify { commentDao.findById(commentId1) }
    }

    @Test
    fun `test createComment with invalid media url`() = runTest {
        val userId1 = 1
        val postId1 = 1
        val text = "New comment"
        val image = "invalid-image.jpg"

        val user1 = UserEntity(
            id = userId1,
            name = "User 1",
            climateZone = ClimateZone.TEMPERATE,
            createdAt = Instant.now().minusSeconds(3600),
            updatedAt = Instant.now().minusSeconds(3600),
        )

        coEvery { userDao.findById(userId1) } returns user1
        coEvery { mediaGrpcClient.validateMediaUrl(userId1, image) } returns false

        val requester = Requester(userId1)
        assertFailsWith<InvalidMediaUrls> {
            commentService.createComment(requester, postId1, null, text, image)
        }

        coVerify { userDao.findById(userId1) }
        coVerify { mediaGrpcClient.validateMediaUrl(userId1, image) }
        coVerify(exactly = 0) { commentDao.insert(any(), any(), any(), any()) }
    }

    @Test
    fun `test updateComment success`() = runTest {
        val now = Instant.now()
        val userId1 = 1
        val commentId1 = 1
        val text = "Updated comment"
        val image = "updated-image.jpg"

        val user1 = UserEntity(
            id = userId1,
            name = "User 1",
            climateZone = ClimateZone.TEMPERATE,
            createdAt = now.minusSeconds(3600),
            updatedAt = now.minusSeconds(3600),
        )

        val updatedComment = CommentEntity(
            id = commentId1,
            userId = userId1,
            parentId = null,
            text = text,
            image = image,
            reactionsCount = 0,
            createdAt = now.minusSeconds(1800),
            updatedAt = now,
        )

        coEvery { commentDao.update(commentId1, userId1, text, image) } just Runs
        coEvery { mediaGrpcClient.validateMediaUrl(userId1, image) } returns true
        coEvery { commentDao.findById(commentId1) } returns updatedComment
        coEvery { userDao.getAllByIds(setOf(userId1)) } returns listOf(user1)
        coEvery { commentReactionDao.getAllByCommentIdsAndUserId(setOf(commentId1), userId1) } returns emptyList()
        coEvery { commentReactionDao.getLatestDistinctByCommentId(setOf(commentId1)) } returns emptyList()

        val requester = Requester(userId1)
        val result = commentService.updateComment(requester, commentId1, text, image)

        assertEquals(text, result.text)
        assertEquals(image, result.image)

        coVerify { commentDao.update(commentId1, userId1, text, image) }
        coVerify { mediaGrpcClient.validateMediaUrl(userId1, image) }
        coVerify { commentDao.findById(commentId1) }
    }

    @Test
    fun `test updateComment with invalid media url`() = runTest {
        val userId1 = 1
        val commentId1 = 1
        val text = "Updated comment"
        val image = "invalid-image.jpg"

        coEvery { commentDao.update(commentId1, userId1, text, image) } just Runs
        coEvery { mediaGrpcClient.validateMediaUrl(userId1, image) } returns false

        val requester = Requester(userId1)
        assertFailsWith<InvalidMediaUrls> {
            commentService.updateComment(requester, commentId1, text, image)
        }

        coVerify { commentDao.update(commentId1, userId1, text, image) }
        coVerify { mediaGrpcClient.validateMediaUrl(userId1, image) }
        coVerify(exactly = 0) { commentDao.findById(any()) }
    }

    @Test
    fun `test deleteComment success`() = runTest {
        val userId1 = 1
        val commentId1 = 1

        val comment = CommentEntity(
            id = commentId1,
            userId = userId1,
            parentId = null,
            text = "Comment to delete",
            image = null,
            reactionsCount = 0,
            createdAt = Instant.now().minusSeconds(1800),
            updatedAt = Instant.now().minusSeconds(1800),
        )

        coEvery { commentDao.findById(commentId1) } returns comment
        coEvery { commentDao.delete(commentId1, userId1) } just Runs

        val requester = Requester(userId1)
        commentService.deleteComment(requester, commentId1)

        coVerify { commentDao.findById(commentId1) }
        coVerify { commentDao.delete(commentId1, userId1) }
    }

    @Test
    fun `test getCommentReactions success`() = runTest {
        val now = Instant.now()
        val userId1 = 1
        val userId2 = 2
        val commentId1 = 1

        val user1 = UserEntity(
            id = userId1,
            name = "User 1",
            climateZone = ClimateZone.TEMPERATE,
            createdAt = now.minusSeconds(3600),
            updatedAt = now.minusSeconds(3600),
        )

        val comment = CommentEntity(
            id = commentId1,
            userId = userId2,
            parentId = null,
            text = "Comment text",
            image = null,
            reactionsCount = 1,
            createdAt = now.minusSeconds(1800),
            updatedAt = now.minusSeconds(1800),
        )

        val reaction = CommentReactionEntity(
            commentId = commentId1,
            userId = userId1,
            reaction = Reaction.THUMBS_UP,
            createdAt = now.minusSeconds(600),
            updatedAt = now.minusSeconds(600),
        )

        coEvery { commentDao.findById(commentId1) } returns comment
        coEvery { commentReactionDao.getAllByCommentId(commentId1, any(), any()) } returns listOf(reaction)
        coEvery { userDao.getAllByIds(setOf(userId1)) } returns listOf(user1)

        val requester = Requester(userId2)
        val result = commentService.getCommentReactions(requester, commentId1, now.toEpochMilli())

        assertEquals(1, result.size)
        assertEquals(user1.name, result[0].user.name)
        assertEquals(Reaction.THUMBS_UP, result[0].reaction)

        coVerify { commentDao.findById(commentId1) }
        coVerify { commentReactionDao.getAllByCommentId(commentId1, any(), any()) }
        coVerify { userDao.getAllByIds(setOf(userId1)) }
    }

    @Test
    fun `test createReaction success`() = runTest {
        val userId1 = 1
        val commentId1 = 1
        val reaction = Reaction.THUMBS_UP

        val user = UserEntity(
            id = userId1,
            name = "User 1",
            climateZone = ClimateZone.TEMPERATE,
            createdAt = Instant.now().minusSeconds(3600),
            updatedAt = Instant.now().minusSeconds(3600),
        )

        coEvery { userDao.findById(userId1) } returns user
        coEvery { commentReactionDao.insert(commentId1, userId1, reaction) } returns commentId1
        coEvery { commentDao.increaseReactionCount(commentId1) } just Runs

        val requester = Requester(userId1)
        commentService.createReaction(requester, commentId1, reaction)

        coVerify { userDao.findById(userId1) }
        coVerify { commentReactionDao.insert(commentId1, userId1, reaction) }
        coVerify { commentDao.increaseReactionCount(commentId1) }
    }

    @Test
    fun `test deleteReaction success`() = runTest {
        val userId1 = 1
        val commentId1 = 1

        coEvery { commentReactionDao.delete(commentId1, userId1) } just Runs
        coEvery { commentDao.decreaseReactionCount(commentId1) } just Runs

        val requester = Requester(userId1)
        commentService.deleteReaction(requester, commentId1)

        coVerify { commentReactionDao.delete(commentId1, userId1) }
        coVerify { commentDao.decreaseReactionCount(commentId1) }
    }
}
