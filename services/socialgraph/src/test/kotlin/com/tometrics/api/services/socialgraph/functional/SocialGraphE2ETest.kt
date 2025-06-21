package com.tometrics.api.services.socialgraph.functional

import com.tometrics.api.services.socialgraph.domain.models.SocialConnections
import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.test.Test
import kotlin.test.assertEquals

class SocialGraphE2ETest : BaseE2ETest() {

//    @Test
//    fun testSocialGraphFlow() = runApp {
//        // Register two users
//        val (user1Id, user1Tokens) = registerAnon()
//        val (user2Id, user2Tokens) = registerAnon()
//
//        // Get user2's connections to find their ID
//        val initialUser2Connections = getAndAssert<SocialConnections>(
//            url = "/api/v1/socialgraph/connections",
//            accessToken = user2Tokens.access
//        )
//
//        // Initially both users should have empty connections
//        val initialUser1Connections = getAndAssert<SocialConnections>(
//            url = "/api/v1/socialgraph/connections",
//            accessToken = user1Token
//        )
//        assertEquals(0, initialUser1Connections.followers.size)
//        assertEquals(0, initialUser1Connections.following.size)
//        assertEquals(0, initialUser2Connections.followers.size)
//        assertEquals(0, initialUser2Connections.following.size)
//
//        // User 1 follows User 2 - we need to get User 2's ID from their connections
//        // First, we need to get User 2's connections to find their ID
//        val user2Id = getUserIdFromConnections(user2Token)
//
//        // Now User 1 follows User 2
//        postAndAssert<Unit, Unit>(
//            url = "/api/v1/socialgraph/follow/$user2Id",
//            accessToken = user1Token,
//            request = null
//        )
//
//        // Check User 1's connections - should be following User 2
//        val user1ConnectionsAfterFollow = getAndAssert<SocialConnections>(
//            url = "/api/v1/socialgraph/connections",
//            accessToken = user1Token
//        )
//        assertEquals(0, user1ConnectionsAfterFollow.followers.size)
//        assertEquals(1, user1ConnectionsAfterFollow.following.size)
//
//        // Check User 2's connections - should have User 1 as follower
//        val user2ConnectionsAfterFollow = getAndAssert<SocialConnections>(
//            url = "/api/v1/socialgraph/connections",
//            accessToken = user2Token
//        )
//        assertEquals(1, user2ConnectionsAfterFollow.followers.size)
//        assertEquals(0, user2ConnectionsAfterFollow.following.size)
//
//        // User 1 unfollows User 2
//        deleteAndAssert<Unit>(
//            url = "/api/v1/socialgraph/follow/$user2Id",
//            accessToken = user1Token
//        )
//
//        // Check User 1's connections - should be empty again
//        val user1ConnectionsAfterUnfollow = getAndAssert<SocialConnections>(
//            url = "/api/v1/socialgraph/connections",
//            accessToken = user1Token
//        )
//        assertEquals(0, user1ConnectionsAfterUnfollow.followers.size)
//        assertEquals(0, user1ConnectionsAfterUnfollow.following.size)
//
//        // Check User 2's connections - should be empty again
//        val user2ConnectionsAfterUnfollow = getAndAssert<SocialConnections>(
//            url = "/api/v1/socialgraph/connections",
//            accessToken = user2Token
//        )
//        assertEquals(0, user2ConnectionsAfterUnfollow.followers.size)
//        assertEquals(0, user2ConnectionsAfterUnfollow.following.size)
//    }

    @Test
    fun testGetConnectionsForSpecificUser() = runApp {
        // Register two users
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

        // Verify User 2's connections
        assertEquals(listOf(user2Id, user3Id), user1Connections.following)
        assertEquals(listOf(user2Id), user1Connections.followers)
        assertEquals(listOf(user1Id), user2Connections.following)
        assertEquals(listOf(user1Id), user2Connections.followers)
        assertEquals(emptyList(), user3Connections.following)
        assertEquals(listOf(user1Id), user3Connections.followers)
    }

    @Test
    fun testStatusEndpoint() = runApp {
        // Status endpoint should be accessible without authentication
        val response = testClient.get("/api/v1/socialgraph/status")
        assertEquals(HttpStatusCode.OK, response.status)
    }

}
