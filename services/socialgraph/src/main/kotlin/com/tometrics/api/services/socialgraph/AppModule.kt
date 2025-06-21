package com.tometrics.api.services.socialgraph

import com.tometrics.api.services.socialgraph.db.DefaultFollowerDao
import com.tometrics.api.services.socialgraph.db.FollowerDao
import com.tometrics.api.services.socialgraph.db.FollowerDb
import com.tometrics.api.services.socialgraph.service.DefaultSocialGraphService
import com.tometrics.api.services.socialgraph.service.SocialGraphService
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import org.jdbi.v3.core.Jdbi
import org.koin.dsl.module

val appModule = module {

    single<Dotenv> {
        dotenv {
            ignoreIfMissing = true
        }
    }

}

val serviceModule = module {

    single<SocialGraphService> {
        DefaultSocialGraphService(
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