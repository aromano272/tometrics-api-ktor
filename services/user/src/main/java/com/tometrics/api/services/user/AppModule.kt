package com.tometrics.api.services.user


import com.tometrics.api.services.user.db.DefaultUserDao
import com.tometrics.api.services.user.db.UserDao
import com.tometrics.api.services.user.db.UserDb
import com.tometrics.api.services.user.services.DefaultUserService
import com.tometrics.api.services.user.services.UserService
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.util.logging.*
import org.jdbi.v3.core.Jdbi
import org.koin.dsl.module

fun appModule(application: Application) = module {

    single<Dotenv> {
        dotenv {
            ignoreIfMissing = true
        }
    }

    // TODO(aromano): move this to commonservice module, along with dotenv for eg.
    factory<Logger> {
        application.environment.log
    }

}

val serviceModule = module {

    single<UserService> {
        DefaultUserService(
            logger = get(),
            dao = get(),
        )
    }

}

val databaseModule = module {

    single<UserDb> {
        val jdbi: Jdbi = get()
        jdbi.onDemand(UserDb::class.java)
    }

    single<UserDao> {
        DefaultUserDao(
            db = get()
        )
    }

}