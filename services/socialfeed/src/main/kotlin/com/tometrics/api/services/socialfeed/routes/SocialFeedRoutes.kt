package com.tometrics.api.services.socialfeed.routes

import com.tometrics.api.auth.domain.models.requireRequester
import com.tometrics.api.common.domain.models.*
import com.tometrics.api.services.socialfeed.routes.models.*
import com.tometrics.api.services.socialfeed.service.CommentService
import com.tometrics.api.services.socialfeed.service.PostService
import io.github.smiley4.ktoropenapi.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.socialFeedRoutes() {
    val postService: PostService by inject()
    val commentService: CommentService by inject()

    get("/socialfeed/status", {
        description = "Check if the social feed service is up and running."
        response {
            HttpStatusCode.OK to {
                description = "Service is up and running."
            }
        }
    }) {
        call.respond(HttpStatusCode.OK)
    }

    authenticate {
        route("/feed", {
            tags = listOf("Social Feed")
        }) {
            get({
                description = "Get the user's social feed."
                request {
                    queryParameter<Millis>("olderThan") {
                        description = "Timestamp to get posts older than this time (in milliseconds)."
                        required = false
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "A list of posts in the user's feed."
                        body<List<PostDto>>()
                    }
                }
            }) {
                val requester = call.requireRequester()
                val olderThan = call.queryParameters["olderThan"]?.toLongOrNull()
                    ?: System.currentTimeMillis()
                val result = postService.getFeed(requester, olderThan)
                call.respond(result)
            }
        }

        get("/posts/{userId}", {
            tags = listOf("Social Feed")
            description = "Get posts by user ID."
            request {
                pathParameter<UserId>("userId") {
                    description = "The ID of the user whose posts to retrieve."
                    required = true
                }
                queryParameter<Millis>("olderThan") {
                    description = "Timestamp to get posts older than this time (in milliseconds)."
                    required = false
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "A list of posts by the specified user."
                    body<List<PostDto>>()
                }
            }
        }) {
            val requester = call.requireRequester()
            val olderThan = call.queryParameters["olderThan"]?.toLongOrNull()
                ?: System.currentTimeMillis()
            val userId = call.pathParameters["userId"]?.toIntOrNull()
                ?: requester.userId
            val result = postService.getAllPostsByUserId(requester, userId, olderThan)
            call.respond(result)
        }

        post("/post", {
            tags = listOf("Social Feed")
            description = "Create a new post."
            request {
                body<CreatePostRequest> {
                    description = "The post details."
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "The created post."
                    body<PostDto>()
                }
            }
        }) {
            val requester = call.requireRequester()
            val request = call.receive<CreatePostRequest>()

            val result = postService.createPost(
                requester = requester,
                locationInfoId = request.locationInfoId,
                images = request.images,
                text = request.text,
            )
            call.respond(result)
        }

        route("/post/{postId}", {
            tags = listOf("Social Feed")
            description = "Operations on a specific post."
            request {
                pathParameter<PostId>("postId") {
                    description = "The ID of the post to operate on."
                    required = true
                }
            }
        }) {

            get({
                description = "Get a specific post by ID."
                response {
                    HttpStatusCode.OK to {
                        description = "The requested post."
                        body<PostDto>()
                    }
                }
            }) {
                val requester = call.requireRequester()
                val postId = call.pathParameters["postId"]?.toIntOrNull() ?: throw ValidationError(listOf(
                    "`postId` is required"
                ))
                val result = postService.getPost(requester, postId)
                call.respond(result)
            }

            delete({
                description = "Delete a specific post."
                response {
                    HttpStatusCode.OK to {
                        description = "Post successfully deleted."
                    }
                }
            }) {
                val requester = call.requireRequester()
                val postId = call.pathParameters["postId"]?.toIntOrNull() ?: throw ValidationError(listOf(
                    "`postId` is required"
                ))
                postService.deletePost(requester, postId)
                call.respond(HttpStatusCode.OK)
            }

            put({
                description = "Update a specific post."
                request {
                    body<PutPostRequest> {
                        description = "The updated post details."
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "The updated post."
                        body<PostDto>()
                    }
                }
            }) {
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
                call.respond(result)
            }

            route("/reaction", {
                tags = listOf("Social Feed")
                description = "Operations on post reactions."
            }) {

                get("/all", {
                    description = "Get all reactions for a specific post."
                    request {
                        queryParameter<Millis>("olderThan") {
                            description = "Timestamp to get posts older than this time (in milliseconds)."
                            required = false
                        }
                    }
                    response {
                        HttpStatusCode.OK to {
                            description = "A list of user reactions for the post."
                            body<List<UserReactionDto>>()
                        }
                    }
                }) {
                    val requester = call.requireRequester()
                    val postId = call.pathParameters["postId"]?.toIntOrNull() ?: throw ValidationError(listOf(
                        "`postId` is required"
                    ))
                    val olderThan = call.queryParameters["olderThan"]?.toLongOrNull()
                        ?: System.currentTimeMillis()
                    val result = postService.getPostReactions(requester, postId, olderThan)
                    call.respond(result)
                }

                post({
                    description = "Create a reaction for a specific post."
                    request {
                        body<CreateReactionRequest> {
                            description = "The reaction details."
                        }
                    }
                    response {
                        HttpStatusCode.OK to {
                            description = "Reaction successfully created."
                        }
                    }
                }) {
                    val requester = call.requireRequester()
                    val postId = call.pathParameters["postId"]?.toIntOrNull() ?: throw ValidationError(listOf(
                        "`postId` is required"
                    ))
                    val request = call.receive<CreateReactionRequest>()
                    postService.createReaction(requester, postId, request.reaction)
                    call.respond(HttpStatusCode.OK)
                }

                delete({
                    description = "Delete a reaction for a specific post."
                    response {
                        HttpStatusCode.OK to {
                            description = "Reaction successfully deleted."
                        }
                    }
                }) {
                    val requester = call.requireRequester()
                    val postId = call.pathParameters["postId"]?.toIntOrNull() ?: throw ValidationError(listOf(
                        "`postId` is required"
                    ))
                    postService.deleteReaction(requester, postId)
                    call.respond(HttpStatusCode.OK)
                }

            }

            route("/comment", {
                tags = listOf("Social Feed")
                description = "Operations on post comments."
            }) {

                get("/all", {
                    description = "Get all comments for a specific post."
                    request {
                        queryParameter<Millis>("olderThan") {
                            description = "Timestamp to get posts older than this time (in milliseconds)."
                            required = false
                        }
                    }
                    response {
                        HttpStatusCode.OK to {
                            description = "A list of comments for the post."
                            body<List<CommentDto>>()
                        }
                    }
                }) {
                    val requester = call.requireRequester()
                    val postId = call.pathParameters["postId"]?.toIntOrNull() ?: throw ValidationError(listOf(
                        "`postId` is required"
                    ))
                    val olderThan = call.queryParameters["olderThan"]?.toLongOrNull()
                        ?: System.currentTimeMillis()

                    val result = commentService.getAllByPostId(requester, postId, olderThan)
                    call.respond(result)
                }

                post({
                    description = "Create a comment for a specific post."
                    request {
                        body<CreateCommentRequest> {
                            description = "The comment details."
                        }
                    }
                    response {
                        HttpStatusCode.OK to {
                            description = "The created comment."
                            body<CommentDto>()
                        }
                    }
                }) {
                    val requester = call.requireRequester()
                    val postId = call.pathParameters["postId"]?.toIntOrNull() ?: throw ValidationError(listOf(
                        "`postId` is required"
                    ))
                    val request = call.receive<CreateCommentRequest>()
                    val result = commentService.createComment(
                        requester = requester,
                        postId = postId,
                        parentId = request.parent,
                        text = request.text,
                        image = request.image,
                    )
                    call.respond(result)
                }

                route("/{commentId}", {
                    tags = listOf("Social Feed")
                    description = "Operations on a specific comment."
                    request {
                        pathParameter<CommentId>("commentId") {
                            description = "The ID of the comment to operate on."
                            required = true
                        }
                    }
                }) {

                    put({
                        description = "Update a specific comment."
                        request {
                            body<PutCommentRequest> {
                                description = "The updated comment details."
                            }
                        }
                        response {
                            HttpStatusCode.OK to {
                                description = "The updated comment."
                                body<CommentDto>()
                            }
                        }
                    }) {
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
                        call.respond(result)
                    }

                    delete({
                        description = "Delete a specific comment."
                        response {
                            HttpStatusCode.OK to {
                                description = "Comment successfully deleted."
                            }
                        }
                    }) {
                        val requester = call.requireRequester()
                        val commentId = call.pathParameters["commentId"]?.toIntOrNull() ?: throw ValidationError(listOf(
                            "`commentId` is required"
                        ))
                        commentService.deleteComment(requester, commentId)
                        call.respond(HttpStatusCode.OK)
                    }

                    route("/reaction", {
                        tags = listOf("Social Feed")
                        description = "Operations on comment reactions."
                    }) {

                        get("/all", {
                            description = "Get all reactions for a specific comment."
                            request {
                                queryParameter<Millis>("olderThan") {
                                    description = "Timestamp to get posts older than this time (in milliseconds)."
                                    required = false
                                }
                            }
                            response {
                                HttpStatusCode.OK to {
                                    description = "A list of user reactions for the comment."
                                    body<List<UserReactionDto>>()
                                }
                            }
                        }) {
                            val requester = call.requireRequester()
                            val commentId = call.pathParameters["commentId"]?.toIntOrNull() ?: throw ValidationError(listOf(
                                "`commentId` is required"
                            ))
                            val olderThan = call.queryParameters["olderThan"]?.toLongOrNull()
                                ?: System.currentTimeMillis()
                            val result = commentService.getCommentReactions(
                                requester = requester,
                                commentId = commentId,
                                olderThan = olderThan,
                            )
                            call.respond(result)
                        }

                        post({
                            description = "Create a reaction for a specific comment."
                            request {
                                body<CreateReactionRequest> {
                                    description = "The reaction details."
                                }
                            }
                            response {
                                HttpStatusCode.OK to {
                                    description = "Reaction successfully created."
                                }
                            }
                        }) {
                            val requester = call.requireRequester()
                            val commentId = call.pathParameters["commentId"]?.toIntOrNull() ?: throw ValidationError(listOf(
                                "`commentId` is required"
                            ))
                            val request = call.receive<CreateReactionRequest>()
                            commentService.createReaction(requester, commentId, request.reaction)
                            call.respond(HttpStatusCode.OK)
                        }

                        delete({
                            description = "Delete a reaction for a specific comment."
                            response {
                                HttpStatusCode.OK to {
                                    description = "Reaction successfully deleted."
                                }
                            }
                        }) {
                            val requester = call.requireRequester()
                            val commentId = call.pathParameters["commentId"]?.toIntOrNull() ?: throw ValidationError(listOf(
                                "`commentId` is required"
                            ))
                            commentService.deleteReaction(requester, commentId)
                            call.respond(HttpStatusCode.OK)
                        }

                    }

                }

            }

        }

    }
}