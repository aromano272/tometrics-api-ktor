package com.tometrics.api.services.socialfeed

import com.tometrics.api.services.commongrpc.services.SocialGraphGrpcService
import com.tometrics.api.services.socialfeed.db.DefaultFollowerDao
import com.tometrics.api.services.socialfeed.db.FollowerDao
import com.tometrics.api.services.socialfeed.db.FollowerDb
import com.tometrics.api.services.socialfeed.service.DefaultSocialGraphGrpcService
import com.tometrics.api.services.socialfeed.service.DefaultSocialGraphService
import com.tometrics.api.services.socialfeed.service.SocialGraphService
import org.jdbi.v3.core.Jdbi
import org.koin.dsl.bind
import org.koin.dsl.module

val serviceModule = module {

    factory<SocialGraphService> {
        DefaultSocialGraphService(
            logger = get(),
            userGrpcClient = get(),
            dao = get(),
        )
    }.bind(SocialGraphGrpcService::class)

    single<DefaultSocialGraphGrpcService> {
        DefaultSocialGraphGrpcService(
            service = get(),
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
