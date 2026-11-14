package com.openfang.api

import com.google.common.truth.Truth.assertThat
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

/**
 * Task 4, Step 1.
 *
 * Pinned by Review Focus #5: a 401 from the chat endpoint MUST emit
 * [ChatChunk.AuthError] and stop the stream — not silently drop.
 *
 * Uses Ktor's [MockEngine] to simulate the OpenFang API responses.
 */
class OpenFangApiClientTest {

    private lateinit var client: OpenFangApiClient
    private lateinit var mockEngine: MockEngine

    @Before
    fun setUp() {
        mockEngine = MockEngine { request ->
            when (request.url.encodedPath) {
                "/health" -> respond(
                    content = """{"ok":true,"version":"0.6.9"}""",
                    headers = headersOf("Content-Type", "application/json"),
                )
                "/api/v1/agents" -> respond(
                    content = """[{"id":"a1","name":"researcher","status":"idle"}]""",
                    headers = headersOf("Content-Type", "application/json"),
                )
                "/api/v1/hands" -> respond(
                    content = """[{"id":"h1","name":"Researcher","status":"active"}]""",
                    headers = headersOf("Content-Type", "application/json"),
                )
                "/api/v1/hands/h1/activate" -> respond(
                    content = """{"id":"h1","name":"Researcher","status":"active"}""",
                    headers = headersOf("Content-Type", "application/json"),
                )
                "/api/v1/hands/h1/pause" -> respond(
                    content = """{"id":"h1","name":"Researcher","status":"paused"}""",
                    headers = headersOf("Content-Type", "application/json"),
                )
                else -> respond(content = "{}", status = HttpStatusCode.OK)
            }
        }
        client = OpenFangApiClient(
            base = "http://127.0.0.1:4200",
            tokenProvider = { null },
            clientFactory = { HttpClient(mockEngine) },
        )
    }

    @Test
    fun testHealth() = runBlocking {
        val h = client.health()
        assertThat(h.ok).isTrue()
        assertThat(h.version).isEqualTo("0.6.9")
    }

    @Test
    fun testAgents() = runBlocking {
        val list = client.agents()
        assertThat(list).hasSize(1)
        assertThat(list[0].name).isEqualTo("researcher")
    }

    @Test
    fun testHands() = runBlocking {
        val list = client.hands()
        assertThat(list).hasSize(1)
        assertThat(list[0].status).isEqualTo("active")
    }

    @Test
    fun testActivateHand() = runBlocking {
        val h = client.activateHand("h1")
        assertThat(h.status).isEqualTo("active")
    }

    @Test
    fun testPauseHand() = runBlocking {
        val h = client.pauseHand("h1")
        assertThat(h.status).isEqualTo("paused")
    }

    @Test
    fun testChatEmitsAuthErrorOn401() = runBlocking {
        // Rebuild the client with an engine that 401s the chat endpoint
        val authEngine = MockEngine { request ->
            if (request.url.encodedPath.contains("chat")) {
                respond(content = "", status = HttpStatusCode.Unauthorized)
            } else {
                respond(content = "{}", status = HttpStatusCode.OK)
            }
        }
        val client401 = OpenFangApiClient(
            base = "http://127.0.0.1:4200",
            tokenProvider = { "stale-token" },
            clientFactory = { HttpClient(authEngine) },
        )
        val chunks = client401.chat("researcher", "hi").take(2).toList()
        // First chunk MUST be AuthError (not a silent drop)
        assertThat(chunks.first()).isInstanceOf(ChatChunk.AuthError::class.java)
    }
}
