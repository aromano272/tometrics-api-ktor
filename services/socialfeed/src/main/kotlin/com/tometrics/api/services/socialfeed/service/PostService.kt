package com.tometrics.api.services.socialfeed.service

import com.tometrics.api.auth.domain.models.Requester
import com.tometrics.api.common.domain.models.ImageUrl
import com.tometrics.api.common.domain.models.LocationInfoId
import com.tometrics.api.common.domain.models.Millis
import com.tometrics.api.common.domain.models.PostId
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.commongrpc.services.UserGrpcClient
import com.tometrics.api.services.socialfeed.db.FollowerDao
import com.tometrics.api.services.socialfeed.domain.models.Post
import com.tometrics.api.services.socialfeed.domain.models.Reaction
import io.ktor.util.logging.*

interface PostService {

    suspend fun getFeed(
        requester: Requester,
        olderThan: Millis,
    ): List<Post>

    suspend fun getAllPostsByUserId(
        requester: Requester,
        userId: UserId,
        olderThan: Millis,
    ): List<Post>

    suspend fun createPost(
        requester: Requester,
        locationInfoId: LocationInfoId?,
        images: List<ImageUrl>,
        text: String,
    ): Post
    suspend fun getPost(requester: Requester, postId: PostId): Post
    suspend fun deletePost(requester: Requester, postId: PostId)
    suspend fun updatePost(
        requester: Requester,
        postId: PostId,
        locationInfoId: LocationInfoId?,
        images: List<ImageUrl>?,
        text: String?,
    ): Post
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
    private val dao: FollowerDao,
) : PostService {

}

