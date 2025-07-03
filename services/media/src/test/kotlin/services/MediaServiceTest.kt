package services

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import aws.smithy.kotlin.runtime.http.request.HttpRequest
import com.tometrics.api.auth.domain.models.Requester
import com.tometrics.api.common.domain.models.UnauthorizedError
import com.tometrics.api.services.commongrpc.models.user.GrpcValidateUsersResult
import com.tometrics.api.services.commongrpc.services.UserGrpcClient
import com.tometrics.api.services.media.services.DefaultMediaService
import io.github.cdimascio.dotenv.Dotenv
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

class MediaServiceTest {

    private val dotenv: Dotenv = mockk {
        coEvery { this@mockk["AWS_S3_BUCKET_USER_UPLOAD"] }
            .returns("AWS_S3_BUCKET_USER_UPLOAD")
    }
    private val s3Client: S3Client = mockk()
    private val userGrpcClient: UserGrpcClient = mockk()
    private val mediaService = DefaultMediaService(dotenv, s3Client, userGrpcClient)

    @Test
    fun `generate presigned url should validate user ids and call s3 sdk`() = runTest {
        val requester = Requester(1)

        val presignedRequest: HttpRequest = mockk {
            every { url.toString() } returns "mock"
        }

        coEvery { userGrpcClient.validateUserIds(setOf(requester.userId)) }
            .returns(GrpcValidateUsersResult.Success)
        mockkStatic("aws.sdk.kotlin.services.s3.presigners.PresignersKt")
        coEvery { s3Client.presignGetObject(any(), any()) } returns presignedRequest

        val result = mediaService.generatePresignedUrl(requester)
        val slot = CapturingSlot<GetObjectRequest>()
        coVerify { s3Client.presignGetObject(capture(slot), 15.minutes) }
        assertEquals("AWS_S3_BUCKET_USER_UPLOAD", slot.captured.bucket)
        assertContains(slot.captured.key.orEmpty(), "${requester.userId}/")
        assertEquals("mock", result)
    }

    @Test
    fun `generate presigned url should validate user ids and fail if not validated`() = runTest {
        val requester = Requester(1)

        mockkStatic("aws.sdk.kotlin.services.s3.presigners.PresignersKt")

        coEvery { userGrpcClient.validateUserIds(setOf(requester.userId)) }
            .returns(GrpcValidateUsersResult.UserIdsNotFound(setOf(requester.userId)))

        assertThrows<UnauthorizedError> { mediaService.generatePresignedUrl(requester) }
        coVerify(inverse = true) { s3Client.presignGetObject(any(), any()) }
    }

    @Test
    fun `validateMediaUrl should validate url and check if object exists`() = runTest {
        val requesterId = 1
        val validUrl = "https://your-custom-domain.com/1/image.jpg"

        coEvery { dotenv["AWS_S3_BUCKET_USER_UPLOAD"] } returns "user-uploads"
        coEvery { s3Client.headObject(any()) } returns mockk()

        val result = mediaService.validateMediaUrl(requesterId, validUrl)
        assertEquals(true, result)
        coVerify { s3Client.headObject(any()) }

        val invalidUrl = "https://invalid-domain.com/1/image.jpg"
        val result2 = mediaService.validateMediaUrl(requesterId, invalidUrl)
        assertEquals(false, result2)

        coEvery { s3Client.headObject(any()) } throws Exception("Object not found")
        val result3 = mediaService.validateMediaUrl(requesterId, validUrl)
        assertEquals(false, result3)
    }
}
