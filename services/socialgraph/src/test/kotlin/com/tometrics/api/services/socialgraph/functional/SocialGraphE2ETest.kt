package com.tometrics.api.services.socialgraph.functional

import com.tometrics.api.services.socialgraph.domain.models.SocialConnections
import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.test.Test
import kotlin.test.assertEquals

class SocialGraphE2ETest : BaseE2ETest() {

    @Test
    fun testGetConnectionsForSpecificUser() = runApp {
        val (user1Id, user1Tokens) = registerAnon()
        val (user2Id, user2Tokens) = registerAnon()
        val (user3Id, user3Tokens) = registerAnon()

        postAndAssert<Unit, Unit>(
            url = "/api/v1/socialgraph/follow/$user2Id",
            accessToken = user1Tokens.access,
            request = null
        )

        postAndAssert<Unit, Unit>(
            url = "/api/v1/socialgraph/follow/$user3Id",
            accessToken = user1Tokens.access,
            request = null
        )

        postAndAssert<Unit, Unit>(
            url = "/api/v1/socialgraph/follow/$user1Id",
            accessToken = user2Tokens.access,
            request = null
        )

        val user1Connections = getAndAssert<SocialConnections>(
            url = "/api/v1/socialgraph/connections",
            accessToken = user1Tokens.access
        )

        val user2Connections = getAndAssert<SocialConnections>(
            url = "/api/v1/socialgraph/connections/$user2Id",
            accessToken = user1Tokens.access
        )

        val user3Connections = getAndAssert<SocialConnections>(
            url = "/api/v1/socialgraph/connections/$user3Id",
            accessToken = user1Tokens.access
        )

        assertEquals(listOf(user2Id, user3Id), user1Connections.following)
        assertEquals(listOf(user2Id), user1Connections.followers)
        assertEquals(listOf(user1Id), user2Connections.following)
        assertEquals(listOf(user1Id), user2Connections.followers)
        assertEquals(emptyList(), user3Connections.following)
        assertEquals(listOf(user1Id), user3Connections.followers)
    }

    @Test
    fun testStatusEndpoint() = runApp {
        val response = testClient.get("/api/v1/socialgraph/status")
        assertEquals(HttpStatusCode.OK, response.status)
    }

}
