package com.tometrics.api.services.socialfeed

import com.tometrics.api.services.socialfeed.db.DefaultFollowerDao
import com.tometrics.api.services.socialfeed.db.FollowerDao
import com.tometrics.api.services.socialfeed.db.FollowerDb
import com.tometrics.api.services.socialfeed.service.DefaultSocialFeedService
import com.tometrics.api.services.socialfeed.service.SocialFeedService
import org.jdbi.v3.core.Jdbi
import org.koin.dsl.module

val serviceModule = module {

    factory<SocialFeedService> {
        DefaultSocialFeedService(
            logger = get(),
            userGrpcClient = get(),
            dao = get(),
        )
    }

}

val databaseModule = module {

    single<FollowerDb> {
        val jdbi: Jdbi = get()
        jdbi.onDemand(FollowerDb::class.java)
    }

    single<FollowerDao> {
        DefaultFollowerDao(
            db = get()
        )
    }

}
