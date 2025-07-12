package functional

import com.auth0.jwt.JWT
import com.tometrics.api.auth.domain.models.Tokens
import com.tometrics.api.common.domain.models.ServiceInfo
import com.tometrics.api.common.domain.models.ServiceType
import com.tometrics.api.common.domain.models.UserId
import com.tometrics.api.common.route.models.LocationInfoDto
import com.tometrics.api.services.commonservicetest.functional.BaseE2ETest
import com.tometrics.api.services.user.module
import com.tometrics.api.services.user.routes.models.GetGeolocationAutocompleteResponse
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class GeolocationE2ETest : BaseE2ETest(
    serviceInfo = SERVICE_INFO,
    module = { module(SERVICE_INFO) },
) {

    companion object {
        val SERVICE_INFO = ServiceInfo(
            prefix = "/user",
            host = "localhost",
            port = 8082,
            type = ServiceType.USER,
        )
    }

    override suspend fun registerAnon(): Pair<UserId, Tokens> {
        val response = testClient.post("/api/v1/auth/anon/register")
        assertEquals(HttpStatusCode.OK, response.status)
        val tokens = response.body<Tokens>()
        val userId = JWT.decode(tokens.access).getClaim("userId").asInt()
        return userId to tokens

    }

    @Test
    fun `test geolocation autocomplete endpoint`() = runApp {
        val (accessToken, _) = registerAnon().second

        val response = getAndAssert<GetGeolocationAutocompleteResponse>("/api/v1/geolocation/autocomplete?searchQuery=london", accessToken)

        assertNotEquals(0, response.locations.size)

        val first = assertNotNull(response.locations.firstOrNull())
        assertEquals("London", first.city)
        assertEquals("United Kingdom", first.country)
    }

    @Test
    fun `test geolocation reverse-geocoding endpoint`() = runApp {
        val (accessToken, _) = registerAnon().second

        val first = getAndAssert<LocationInfoDto>("/api/v1/geolocation/reverse-geocoding?lat=51.5074&lon=-0.1278", accessToken)

        assertEquals("City of Westminster", first.city)
        assertEquals("United Kingdom", first.country)

        val missingLatResponse = testClient.get("/api/v1/geolocation/reverse-geocoding") {
            bearerAuth(accessToken)
        }
        assertEquals(HttpStatusCode.BadRequest, missingLatResponse.status)

        val missingLonResponse = testClient.get("/api/v1/geolocation/reverse-geocoding?lat=51.5074") {
            bearerAuth(accessToken)
        }
        assertEquals(HttpStatusCode.BadRequest, missingLonResponse.status)
    }

}
