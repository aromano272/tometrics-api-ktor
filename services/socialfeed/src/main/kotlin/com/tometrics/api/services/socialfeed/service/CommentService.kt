package com.tometrics.api.services.socialfeed.service

import com.tometrics.api.auth.domain.models.Requester
import com.tometrics.api.common.domain.models.*
import com.tometrics.api.services.commongrpc.models.user.toDomain
import com.tometrics.api.services.commongrpc.services.MediaGrpcClient
import com.tometrics.api.services.commongrpc.services.UserGrpcClient
import com.tometrics.api.services.socialfeed.db.CommentDao
import com.tometrics.api.services.socialfeed.db.CommentReactionDao
import com.tometrics.api.services.socialfeed.db.PostDao
import com.tometrics.api.services.socialfeed.db.UserDao
import com.tometrics.api.services.socialfeed.db.models.CommentEntity
import com.tometrics.api.services.socialfeed.db.models.UserEntity
import com.tometrics.api.services.socialfeed.domain.models.*
import com.tometrics.api.services.socialfeed.routes.models.CommentDto
import com.tometrics.api.services.socialfeed.routes.models.UserReactionDto
import com.tometrics.api.services.socialfeed.routes.models.toDto
import io.ktor.util.logging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

interface CommentService {

    suspend fun getAllByPostId(
        requester: Requester,
        postId: PostId,
        olderThan: Millis,
    ): List<CommentDto>

    suspend fun createComment(
        requester: Requester,
        postId: PostId,
        parentId: CommentId?,
        text: String,
        image: ImageUrl?,
    ): CommentDto

    suspend fun updateComment(
        requester: Requester,
        commentId: CommentId,
        text: String,
        image: ImageUrl?,
    ): CommentDto

    suspend fun deleteComment(
        requester: Requester,
        commentId: CommentId,
    )

    suspend fun getCommentReactions(
        requester: Requester,
        commentId: CommentId,
        olderThan: Millis,
    ): List<UserReactionDto>

    suspend fun createReaction(
        requester: Requester,
        commentId: CommentId,
        reaction: Reaction,
    )

    suspend fun deleteReaction(
        requester: Requester,
        commentId: CommentId,
    )

}

class DefaultCommentService(
    private val logger: Logger,
    private val userGrpcClient: UserGrpcClient,
    private val mediaGrpcClient: MediaGrpcClient,
    private val userDao: UserDao,
    private val postDao: PostDao,
    private val commentDao: CommentDao,
    private val commentReactionDao: CommentReactionDao,
) : CommentService {

    private suspend fun getAuxiliaryCommentDataAndMapToDto(
        requester: Requester,
        comments: List<CommentEntity>
    ): List<CommentDto> = withContext(Dispatchers.Default) {
        val commentIds = comments.map { it.id }.toSet()
        val users = userDao.getAllByIds(commentIds)
            .associateBy { it.id }
        val myReactions = commentReactionDao.getAllByCommentIdsAndUserId(
            commentIds = commentIds,
            userId = requester.userId,
        ).associateBy { it.commentId }
            .mapValues { (_, entity) -> entity.reaction }
        val topReactions = commentReactionDao.getLatestDistinctByCommentId(commentIds)
            .groupBy { it.commentId }
            .mapValues { (_, entities) -> entities.map { it.reaction} }

        val dtos = comments.mapNotNull { comment ->
            val user = users[comment.userId] ?: return@mapNotNull null.also {
                logger.error("User ${comment.userId} not found for Comment ${comment.id}")
            }

            comment.toDto(
                user = user.toDto(null),
                myReaction = myReactions[comment.id],
                topReactions = topReactions[comment.id].orEmpty(),
            )
        }
        dtos
    }

    override suspend fun getAllByPostId(
        requester: Requester,
        postId: PostId,
        olderThan: Millis,
    ): List<CommentDto> {
        val comments = commentDao.getAllByPostId(
            postId = postId,
            olderThan = Instant.ofEpochMilli(olderThan),
            pageSize = 20,
        )
        return getAuxiliaryCommentDataAndMapToDto(requester, comments)
    }

    override suspend fun createComment(
        requester: Requester,
        postId: PostId,
        parentId: CommentId?,
        text: String,
        image: ImageUrl?,
    ): CommentDto {
        val user = getOrFetchAndSaveUserOrFail(requester.userId)

        image?.let {
            if (!mediaGrpcClient.validateMediaUrl(requester.userId, it))
                throw InvalidMediaUrls
        }

        val commentId = commentDao.insert(
            userId = user.id,
            parentId = parentId,
            text = text,
            image = image,
        ) ?: throw CreateCommentFailed
        val comment = commentDao.findById(commentId) ?: throw CreateCommentFailed
        return getAuxiliaryCommentDataAndMapToDto(requester, listOf(comment)).first()
    }

    override suspend fun updateComment(
        requester: Requester,
        commentId: CommentId,
        text: String,
        image: ImageUrl?,
    ): CommentDto {
        commentDao.update(
            id = commentId,
            userId = requester.userId,
            newText = text,
            newImage = image,
        )

        image?.let {
            if (!mediaGrpcClient.validateMediaUrl(requester.userId, it))
                throw InvalidMediaUrls
        }

        val comment = commentDao.findById(commentId) ?: throw CommentNotFound(commentId)
        val dto = getAuxiliaryCommentDataAndMapToDto(requester, listOf(comment)).first()
        return dto
    }

    override suspend fun deleteComment(
        requester: Requester,
        commentId: CommentId,
    ) {
        val comment = commentDao.findById(commentId) ?: CommentNotFound(commentId)
        commentDao.delete(commentId, requester.userId)
    }

    override suspend fun getCommentReactions(
        requester: Requester,
        commentId: CommentId,
        olderThan: Millis,
    ): List<UserReactionDto> {
        val comment = commentDao.findById(commentId) ?: throw CommentNotFound(commentId)
        val reactions = commentReactionDao.getAllByCommentId(commentId, Instant.ofEpochMilli(olderThan), 20)
        val usersMap = reactions.map { it.userId }.toSet()
            .let { userDao.getAllByIds(it) }
            .associateBy { it.id }
        return reactions.mapNotNull { userReaction ->
            val user = usersMap[userReaction.userId]
            if (user == null) {
                logger.error("User ${userReaction.userId} not found in users table for CommentReaction ${userReaction.commentId}")
                return@mapNotNull null
            }
            UserReactionDto(
                user = user.toDto(location = null),
                reaction = userReaction.reaction,
            )
        }
    }

    override suspend fun createReaction(
        requester: Requester,
        commentId: CommentId,
        reaction: Reaction,
    ) {
        val user = getOrFetchAndSaveUserOrFail(requester.userId)
        commentReactionDao.insert(
            commentId = commentId,
            userId = user.id,
            reaction = reaction,
        )
        commentDao.increaseReactionCount(commentId)
    }

    override suspend fun deleteReaction(
        requester: Requester,
        commentId: CommentId,
    ) {
        commentReactionDao.delete(commentId, requester.userId)
        commentDao.decreaseReactionCount(commentId)
    }

    private suspend fun getOrFetchAndSaveUserOrFail(id: UserId): UserEntity =
        userDao.findById(id)
            ?: userGrpcClient.findUserById(id)?.let { user ->
                val id = userDao.insert(
                    id = user.id,
                    name = user.name,
                    climateZone = user.climateZone?.toDomain(),
                )
                id?.let { userDao.findById(it) }
            }
            ?: throw UserNotFound(id)

}