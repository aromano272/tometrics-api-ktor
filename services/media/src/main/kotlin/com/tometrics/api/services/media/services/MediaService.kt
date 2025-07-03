package com.tometrics.api.services.media.services

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.HeadObjectRequest
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import com.tometrics.api.auth.domain.models.Requester
import com.tometrics.api.common.domain.models.UnauthorizedError
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.services.commongrpc.models.user.GrpcValidateUsersResult
import com.tometrics.api.services.commongrpc.services.MediaGrpcService
import com.tometrics.api.services.commongrpc.services.UserGrpcClient
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.http.*
import java.util.*
import kotlin.time.Duration.Companion.minutes

interface MediaService : MediaGrpcService {
    suspend fun generatePresignedUrl(requester: Requester): String
}

class DefaultMediaService(
    private val dotenv: Dotenv,
    private val s3Client: S3Client,
    private val userGrpcClient: UserGrpcClient,
) : MediaService {

    override suspend fun validateMediaUrl(requesterId: UserId, url: String): Boolean {
        val userUploadBucket = dotenv["AWS_S3_BUCKET_USER_UPLOAD"]
        return try {
            val url = Url(url)

            val host = url.host
            // PRODREADINESS: domain & aws api keys
            if (!host.endsWith("your-custom-domain.com")) {
                return false
            }

            val path = url.encodedPath.removePrefix("/")
            val userIdPrefix = path.substringBefore("/")

            if (userIdPrefix != requesterId.toString()) {
                return false
            }

            val headRequest = HeadObjectRequest {
                bucket = userUploadBucket
                key = path
            }

            s3Client.headObject(headRequest)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun generatePresignedUrl(requester: Requester): String {
        userGrpcClient.validateUserIds(setOf(requester.userId))
            .takeIf { it !is GrpcValidateUsersResult.Success }
            ?.let { throw UnauthorizedError("") }

        val userUploadBucket = dotenv["AWS_S3_BUCKET_USER_UPLOAD"]
        val unsignedRequest = GetObjectRequest {
            bucket = userUploadBucket
            key = "${requester.userId}/${UUID.randomUUID()}"
        }

        val presignedRequest = s3Client.presignGetObject(unsignedRequest, 15.minutes)
        return presignedRequest.url.toString()
    }

}