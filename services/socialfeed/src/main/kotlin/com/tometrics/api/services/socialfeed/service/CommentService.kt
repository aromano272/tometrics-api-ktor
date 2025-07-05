package com.tometrics.api.services.socialfeed.service

import com.tometrics.api.auth.domain.models.Requester
import com.tometrics.api.common.domain.models.CommentId
import com.tometrics.api.common.domain.models.ImageUrl
import com.tometrics.api.common.domain.models.Millis
import com.tometrics.api.common.domain.models.PostId
import com.tometrics.api.services.socialfeed.domain.models.Comment
import com.tometrics.api.services.socialfeed.domain.models.Reaction

interface CommentService {

    suspend fun getAllByPostId(
        requester: Requester,
        postId: PostId,
        olderThan: Millis,
    ): List<Comment>

    suspend fun createComment(
        requester: Requester,
        postId: PostId,
        parent: CommentId?,
        text: String,
        image: ImageUrl?,
    ): Comment

    suspend fun updateComment(
        requester: Requester,
        commentId: CommentId,
        text: String?,
        image: ImageUrl?,
    ): Comment

    suspend fun deleteComment(
        requester: Requester,
        commentId: CommentId,
    )

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

) : CommentService {



}