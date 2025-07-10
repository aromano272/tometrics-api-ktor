package com.tometrics.api.services.socialfeed

import com.tometrics.api.services.socialfeed.db.*
import com.tometrics.api.services.socialfeed.service.CommentService
import com.tometrics.api.services.socialfeed.service.DefaultCommentService
import com.tometrics.api.services.socialfeed.service.DefaultPostService
import com.tometrics.api.services.socialfeed.service.PostService
import org.jdbi.v3.core.Jdbi
import org.koin.dsl.module

val serviceModule = module {

    factory<PostService> {
        DefaultPostService(
            logger = get(),
            userGrpcClient = get(),
            mediaGrpcClient = get(),
            postDao = get(),
            userDao = get(),
            locationInfoDao = get(),
            postReactionDao = get(),
        )
    }

    factory<CommentService> {
        DefaultCommentService(

        )
    }

}

val databaseModule = module {

    single<PostDb> {
        val jdbi: Jdbi = get()
        jdbi.onDemand(PostDb::class.java)
    }

    single<PostDao> {
        DefaultPostDao(
            db = get()
        )
    }

    single<PostReactionDb> {
        val jdbi: Jdbi = get()
        jdbi.onDemand(PostReactionDb::class.java)
    }

    single<PostReactionDao> {
        DefaultPostReactionDao(
            db = get()
        )
    }

    single<CommentDb> {
        val jdbi: Jdbi = get()
        jdbi.onDemand(CommentDb::class.java)
    }

    single<CommentDao> {
        DefaultCommentDao(
            db = get()
        )
    }

    single<CommentReactionDb> {
        val jdbi: Jdbi = get()
        jdbi.onDemand(CommentReactionDb::class.java)
    }

    single<CommentReactionDao> {
        DefaultCommentReactionDao(
            db = get()
        )
    }

    single<UserDb> {
        val jdbi: Jdbi = get()
        jdbi.onDemand(UserDb::class.java)
    }

    single<UserDao> {
        DefaultUserDao(
            db = get()
        )
    }

    single<LocationInfoDb> {
        val jdbi: Jdbi = get()
        jdbi.onDemand(LocationInfoDb::class.java)
    }

    single<LocationInfoDao> {
        DefaultLocationInfoDao(
            db = get()
        )
    }

}
