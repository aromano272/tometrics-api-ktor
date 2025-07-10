package com.tometrics.api.services.media


import aws.sdk.kotlin.services.s3.S3Client
import com.tometrics.api.services.media.services.DefaultMediaGrpcService
import com.tometrics.api.services.media.services.DefaultMediaService
import com.tometrics.api.services.media.services.MediaService
import io.github.cdimascio.dotenv.Dotenv
import org.koin.dsl.module

val appModule = module {

    single<S3Client> {
        val dotenv: Dotenv = get()
        val awsRegion = dotenv["AWS_REGION"]
        S3Client { region = awsRegion }
    }

}

val serviceModule = module {

    single<MediaService> {
        DefaultMediaService(
            dotenv = get(),
            s3Client = get(),
            userGrpcClient = get(),
        )
    }

    single<DefaultMediaGrpcService> {
        DefaultMediaGrpcService(
            service = get(),
        )
    }

}


