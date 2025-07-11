package com.tometrics.api.services.socialgraph

import com.tometrics.api.services.socialgraph.db.DefaultFollowerDao
import com.tometrics.api.services.socialgraph.db.FollowerDao
import com.tometrics.api.services.socialgraph.db.FollowerDb
import com.tometrics.api.services.socialgraph.service.DefaultSocialGraphGrpcService
import com.tometrics.api.services.socialgraph.service.DefaultSocialGraphService
import com.tometrics.api.services.socialgraph.service.SocialGraphService
import org.jdbi.v3.core.Jdbi
import org.koin.dsl.module

val serviceModule = module {

    factory<SocialGraphService> {
        DefaultSocialGraphService(
            logger = get(),
            userGrpcClient = get(),
            dao = get(),
        )
    }

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
