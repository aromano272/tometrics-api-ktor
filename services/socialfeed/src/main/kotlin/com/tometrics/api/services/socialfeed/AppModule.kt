package com.tometrics.api.services.socialfeed

import com.tometrics.api.services.socialfeed.db.DefaultLocationInfoDao
import com.tometrics.api.services.socialfeed.db.DefaultPostDao
import com.tometrics.api.services.socialfeed.db.DefaultPostReactionDao
import com.tometrics.api.services.socialfeed.db.DefaultUserDao
import com.tometrics.api.services.socialfeed.db.LocationInfoDao
import com.tometrics.api.services.socialfeed.db.LocationInfoDb
import com.tometrics.api.services.socialfeed.db.PostDao
import com.tometrics.api.services.socialfeed.db.PostDb
import com.tometrics.api.services.socialfeed.db.PostReactionDao
import com.tometrics.api.services.socialfeed.db.PostReactionDb
import com.tometrics.api.services.socialfeed.db.UserDao
import com.tometrics.api.services.socialfeed.db.UserDb
import com.tometrics.api.services.socialfeed.service.CommentService
import com.tometrics.api.services.socialfeed.service.DefaultCommentService
import com.tometrics.api.services.socialfeed.service.DefaultPostService
import com.tometrics.api.services.socialfeed.service.PostService
import org.jdbi.v3.core.Jdbi
import org.koin.dsl.module
import kotlin.jvm.java

val serviceModule = module {

    factory<PostService> {
        DefaultPostService(
            logger = get(),
            userGrpcClient = get(),
            dao = get(),
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
