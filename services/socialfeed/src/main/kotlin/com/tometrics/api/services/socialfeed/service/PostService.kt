package com.tometrics.api.services.socialfeed.service

import com.tometrics.api.auth.domain.models.Requester
import com.tometrics.api.common.domain.models.*
import com.tometrics.api.services.commongrpc.models.user.GrpcValidateMediaUrlsResult
import com.tometrics.api.services.commongrpc.models.user.toDomain
import com.tometrics.api.services.commongrpc.services.MediaGrpcClient
import com.tometrics.api.services.commongrpc.services.UserGrpcClient
import com.tometrics.api.services.socialfeed.db.LocationInfoDao
import com.tometrics.api.services.socialfeed.db.PostDao
import com.tometrics.api.services.socialfeed.db.PostReactionDao
import com.tometrics.api.services.socialfeed.db.UserDao
import com.tometrics.api.services.socialfeed.db.models.LocationInfoEntity
import com.tometrics.api.services.socialfeed.db.models.PostEntity
import com.tometrics.api.services.socialfeed.db.models.UserEntity
import com.tometrics.api.services.socialfeed.domain.models.*
import com.tometrics.api.services.socialfeed.routes.models.PostDto
import com.tometrics.api.services.socialfeed.routes.models.UserReactionDto
import com.tometrics.api.services.socialfeed.routes.models.toDto
import io.ktor.util.logging.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

interface PostService {

    suspend fun getFeed(
        requester: Requester,
        olderThan: Millis,
    ): List<PostDto>

    suspend fun getAllPostsByUserId(
        requester: Requester,
        userId: UserId,
        olderThan: Millis,
    ): List<PostDto>

    suspend fun createPost(
        requester: Requester,
        locationInfoId: LocationInfoId?,
        images: List<ImageUrl>,
        text: String,
    ): PostDto
    suspend fun getPost(requester: Requester, postId: PostId): PostDto
    suspend fun deletePost(requester: Requester, postId: PostId)
    suspend fun updatePost(
        requester: Requester,
        postId: PostId,
        locationInfoId: LocationInfoId?,
        images: List<ImageUrl>,
        text: String,
    ): PostDto
    suspend fun getPostReactions(
        requester: Requester,
        postId: PostId,
        olderThan: Millis,
    ): List<UserReactionDto>
    suspend fun createReaction(
        requester: Requester,
        postId: PostId,
        reaction: Reaction,
    )
    suspend fun deleteReaction(
        requester: Requester,
        postId: PostId,
    )

}

class DefaultPostService(
    private val logger: Logger,
    private val userGrpcClient: UserGrpcClient,
    private val mediaGrpcClient: MediaGrpcClient,
    private val postDao: PostDao,
    private val userDao: UserDao,
    private val locationInfoDao: LocationInfoDao,
    private val postReactionDao: PostReactionDao,
) : PostService {

    private suspend fun getAuxiliaryPostDataAndMapToDto(
        requester: Requester,
        posts: List<PostEntity>
    ): List<PostDto> = withContext(Dispatchers.Default) {
        // TODO(aromano): to much manual joining, i should consider doing this in sql
        val postIds = posts.map { it.id }.toSet()
        val users = userDao.getAllByIds(posts.map { it.userId }.toSet())
            .associateBy { it.id }
        val locations = locationInfoDao.getAllByIds(posts.mapNotNull { it.locationId }.toSet())
            .associateBy { it.locationId }
        val myReactions = postReactionDao.getAllByPostIdsAndUserId(
            postIds = postIds,
            userId = requester.userId,
        ).associateBy { it.postId }
            .mapValues { (_, entity) -> entity.reaction }
        val topReactions = postReactionDao.getLatestDistinctByPostId(postIds)
            .groupBy { it.postId }
            .mapValues { (_, entities) -> entities.map { it.reaction } }

        val dtos = posts.mapNotNull { post ->
            val user = users[post.userId] ?: return@mapNotNull null.also {
                logger.error("User ${post.userId} not found for Post ${post.id}")
            }
            val location = post.locationId?.let { locationId ->
                locations[locationId].also {
                    if (it == null) logger.error("Location $locationId not found for User ${user.id}")
                }
            }

            post.toDto(
                // NOTE(aromano): it would be unnecessary to send user's location as there's already the posts location
                user = user.toDto(null),
                locationInfo = location?.toDto(),
                myReaction = myReactions[post.id],
                topReactions = topReactions[post.id].orEmpty(),
            )
        }
        dtos
    }

    override suspend fun getFeed(
        requester: Requester,
        olderThan: Millis,
    ): List<PostDto> {
        val posts = postDao.getFeed(Instant.ofEpochMilli(olderThan))
        val dtos = getAuxiliaryPostDataAndMapToDto(requester, posts)
        return dtos
    }

    override suspend fun getAllPostsByUserId(
        requester: Requester,
        userId: UserId,
        olderThan: Millis,
    ): List<PostDto> {
        val posts = postDao.getAllByUserId(userId, Instant.ofEpochMilli(olderThan))
        val dtos = getAuxiliaryPostDataAndMapToDto(requester, posts)
        return dtos
    }

    private suspend fun getLocationOrFetchAndSaveOrFail(locationId: LocationInfoId): LocationInfoEntity =
        locationInfoDao.findById(locationId)
            ?: userGrpcClient.findLocationById(locationId)
                ?.let {
                    val id = locationInfoDao.insert(
                        locationId = it.id,
                        city = it.city,
                        country = it.country,
                        countryCode = it.countryCode,
                    )
                    id?.let { locationInfoDao.findById(it) }
                }
            ?: throw LocationNotFound(locationId)

    // TODO(aromano): refactor most of the service code to do db stuff inside a transaction, because
    // something may fail and we'd be left in an invalid state
    override suspend fun createPost(
        requester: Requester,
        locationInfoId: LocationInfoId?,
        images: List<ImageUrl>,
        text: String,
    ): PostDto {
        val user = getOrFetchAndSaveUserOrFail(requester.userId)

        val locationInfo = locationInfoId?.let { getLocationOrFetchAndSaveOrFail(it) }

        val validateMediaUrlResult = mediaGrpcClient.validateMediaUrls(requester.userId, images.toSet())
        if (validateMediaUrlResult !is GrpcValidateMediaUrlsResult.Success) {
            throw InvalidMediaUrls
        }

        val postId = postDao.insert(
            userId = user.id,
            locationId = locationInfo?.locationId,
            images = images,
            text = text,
        ) ?: throw CreatePostFailed
        val post = postDao.findById(postId) ?: throw CreatePostFailed
        return getAuxiliaryPostDataAndMapToDto(requester, listOf(post)).first()
    }

    override suspend fun getPost(requester: Requester, postId: PostId): PostDto {
        val post = postDao.findById(postId) ?: throw PostNotFound(postId)
        val dto = getAuxiliaryPostDataAndMapToDto(requester, listOf(post)).first()
        return dto
    }

    override suspend fun deletePost(requester: Requester, postId: PostId) {
        val post = postDao.findById(postId) ?: throw PostNotFound(postId)
        postDao.delete(postId, requester.userId)
    }

    override suspend fun updatePost(
        requester: Requester,
        postId: PostId,
        locationInfoId: LocationInfoId?,
        images: List<ImageUrl>,
        text: String,
    ): PostDto {
        val locationInfo = locationInfoId?.let { getLocationOrFetchAndSaveOrFail(it) }

        val validateMediaUrlResult = mediaGrpcClient.validateMediaUrls(requester.userId, images.toSet())
        if (validateMediaUrlResult !is GrpcValidateMediaUrlsResult.Success) {
            throw InvalidMediaUrls
        }

        postDao.update(
            id = postId,
            userId = requester.userId,
            locationId = locationInfo?.locationId,
            newImages = images,
            newText = text,
        )
        val post = postDao.findById(postId) ?: throw PostNotFound(postId)
        val dto = getAuxiliaryPostDataAndMapToDto(requester, listOf(post)).first()
        return dto
    }

    override suspend fun getPostReactions(
        requester: Requester,
        postId: PostId,
        olderThan: Millis
    ): List<UserReactionDto> {
        val post = postDao.findById(postId) ?: throw PostNotFound(postId)
        val reactions = postReactionDao.getAllByPostId(postId, Instant.ofEpochMilli(olderThan), 20)
        val usersMap = reactions.map { it.userId }.toSet()
            .let { userDao.getAllByIds(it) }
            .associateBy { it.id }
        return reactions.mapNotNull { userReaction ->
            val user = usersMap[userReaction.userId]
            if (user == null) {
                logger.error("User ${userReaction.userId} not found in users table for PostReaction ${userReaction.postId}")
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
        postId: PostId,
        reaction: Reaction,
    ) {
        val user = getOrFetchAndSaveUserOrFail(requester.userId)
        postReactionDao.insert(
            postId = postId,
            userId = user.id,
            reaction = reaction,
        )
        postDao.increaseReactionCount(postId)
    }

    override suspend fun deleteReaction(
        requester: Requester,
        postId: PostId,
    ) {
        postReactionDao.delete(
            postId = postId,
            userId = requester.userId,
        )
        postDao.decreaseReactionCount(postId)
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