package com.tometrics.api.services.commonservice

import com.tometrics.api.common.domain.models.CommentId
import com.tometrics.api.common.domain.models.PostId
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.common.route.models.UserDto

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RoutingKey(val value: String)

sealed interface Message {

    @RoutingKey("socialfeed.post_created")
    data class PostCreated(
        val id: PostId,
        val user: UserDto,
        val text: String,
    ) : Message

    @RoutingKey("socialfeed.comment_created")
    data class CommentCreated(
        val id: CommentId,
        val postId: PostId,
        val postUserId: UserId,
        val user: UserDto,
        val text: String,
    ) : Message

}
