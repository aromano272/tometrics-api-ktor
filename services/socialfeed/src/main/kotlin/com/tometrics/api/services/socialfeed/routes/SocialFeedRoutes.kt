package com.tometrics.api.services.socialfeed.routes

import com.tometrics.api.auth.domain.models.requireRequester
import com.tometrics.api.common.domain.models.ValidationError
import com.tometrics.api.services.socialfeed.routes.models.*
import com.tometrics.api.services.socialfeed.service.CommentService
import com.tometrics.api.services.socialfeed.service.PostService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.socialFeedRoutes() {
    val postService: PostService by inject()
    val commentService: CommentService by inject()

    get("/status") {
        call.respond(HttpStatusCode.OK)
    }

    authenticate {

        // TODO(aromano): paged
        get("/feed") {
            val requester = call.requireRequester()
            val olderThan = call.queryParameters["olderThan"]?.toLongOrNull()
                ?: System.currentTimeMillis()
            val result = postService.getFeed(requester, olderThan)
        }

        // TODO(aromano): paged
        get("/posts/{userId}") {
            val requester = call.requireRequester()
            val olderThan = call.queryParameters["olderThan"]?.toLongOrNull()
                ?: System.currentTimeMillis()
            val userId = call.queryParameters["userId"]?.toIntOrNull()
                ?: requester.userId
            val result = postService.getAllPostsByUserId(requester, userId, olderThan)
        }

        post("/post") {
            val requester = call.requireRequester()
            val request = call.receive<CreatePostRequest>()

            val result = postService.createPost(
                requester = requester,
                locationInfoId = request.locationInfoId,
                images = request.images,
                text = request.text,
            )
        }

        route("/post/{postId}") {

            get {
                val requester = call.requireRequester()
                val postId = call.pathParameters["postId"]?.toIntOrNull() ?: throw ValidationError(listOf(
                    "`postId` is required"
                ))
                val result = postService.getPost(requester, postId)
            }

            delete {
                val requester = call.requireRequester()
                val postId = call.pathParameters["postId"]?.toIntOrNull() ?: throw ValidationError(listOf(
                    "`postId` is required"
                ))
                postService.deletePost(requester, postId)
                call.respond(HttpStatusCode.OK)
            }

            put {
                val requester = call.requireRequester()
                val postId = call.pathParameters["postId"]?.toIntOrNull() ?: throw ValidationError(listOf(
                    "`postId` is required"
                ))
                val request = call.receive<PutPostRequest>()
                val result = postService.updatePost(
                    requester = requester,
                    postId = postId,
                    locationInfoId = request.locationInfoId,
                    images = request.images,
                    text = request.text,
                )
            }

            post("/reaction") {
                val requester = call.requireRequester()
                val postId = call.pathParameters["postId"]?.toIntOrNull() ?: throw ValidationError(listOf(
                    "`postId` is required"
                ))
                val request = call.receive<CreateReactionRequest>()
                postService.createReaction(requester, postId, request.reaction)
            }

            delete("/reaction") {
                val requester = call.requireRequester()
                val postId = call.pathParameters["postId"]?.toIntOrNull() ?: throw ValidationError(listOf(
                    "`postId` is required"
                ))
                postService.deleteReaction(requester, postId)
            }

            // TODO(aromano): paged
            get("/comments") {
                val requester = call.requireRequester()
                val postId = call.pathParameters["postId"]?.toIntOrNull() ?: throw ValidationError(listOf(
                    "`postId` is required"
                ))
                val olderThan = call.queryParameters["olderThan"]?.toLongOrNull()
                    ?: System.currentTimeMillis()

                val result = commentService.getAllByPostId(requester, postId, olderThan)
            }

            post("/comment") {
                val requester = call.requireRequester()
                val postId = call.pathParameters["postId"]?.toIntOrNull() ?: throw ValidationError(listOf(
                    "`postId` is required"
                ))
                val request = call.receive<CreateCommentRequest>()
                val result = commentService.createComment(
                    requester = requester,
                    postId = postId,
                    parent = request.parent,
                    text = request.text,
                    image = request.image,
                )
            }

        }

        route("/comment/{commentId}") {

            put {
                val requester = call.requireRequester()
                val commentId = call.pathParameters["commentId"]?.toIntOrNull() ?: throw ValidationError(listOf(
                    "`commentId` is required"
                ))
                val request = call.receive<PutCommentRequest>()
                val result = commentService.updateComment(
                    requester = requester,
                    commentId = commentId,
                    text = request.text,
                    image = request.image,
                )
            }

            delete {
                val requester = call.requireRequester()
                val commentId = call.pathParameters["commentId"]?.toIntOrNull() ?: throw ValidationError(listOf(
                    "`commentId` is required"
                ))
                commentService.deleteComment(requester, commentId)
            }

            post("/reaction") {
                val requester = call.requireRequester()
                val commentId = call.pathParameters["commentId"]?.toIntOrNull() ?: throw ValidationError(listOf(
                    "`commentId` is required"
                ))
                val request = call.receive<CreateReactionRequest>()
                commentService.createReaction(requester, commentId, request.reaction)
            }

            delete("/reaction") {
                val requester = call.requireRequester()
                val commentId = call.pathParameters["commentId"]?.toIntOrNull() ?: throw ValidationError(listOf(
                    "`commentId` is required"
                ))
                commentService.deleteReaction(requester, commentId)
            }

        }

    }
}
