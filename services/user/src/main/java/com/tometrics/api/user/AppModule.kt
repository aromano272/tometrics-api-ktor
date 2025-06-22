package com.tometrics.api.user


import com.tometrics.api.user.db.DefaultUserDao
import com.tometrics.api.user.db.UserDao
import com.tometrics.api.user.db.UserDb
import com.tometrics.api.user.services.DefaultUserService
import com.tometrics.api.user.services.UserService
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

    single<UserService> {
        DefaultUserService(
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