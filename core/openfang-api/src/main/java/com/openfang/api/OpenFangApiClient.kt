package com.openfang.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

/**
 * Ktor-based [OpenFangApi].
 *
 * @param base  e.g. "http://127.0.0.1:4200"
 * @param tokenProvider  called before each request; returns null or a Bearer token
 * @param clientFactory  injectable for tests (MockEngine). Production uses CIO.
 *
 * The 401 handling in [chat] is the key contract: on
 * Unauthorized, the flow emits [ChatChunk.AuthError] and stops.
 * Never silently drop.
 */
class OpenFangApiClient(
    private val base: String,
    private val tokenProvider: () -> String?,
    private val clientFactory: () -> HttpClient = {
        HttpClient {
            expectSuccess = false
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    },
) : OpenFangApi {

    private val json = Json { ignoreUnknownKeys = true }

    private suspend fun HttpClient.authedGet(path: String): HttpResponse =
        get(base + path) {
            tokenProvider()?.let { header("Authorization", "Bearer $it") }
        }

    private suspend fun HttpClient.authedPost(path: String, body: Any): HttpResponse =
        post(base + path) {
            contentType(ContentType.Application.Json)
            tokenProvider()?.let { header("Authorization", "Bearer $it") }
            setBody(body)
        }

    override suspend fun health(): HealthDto {
        val resp = clientFactory().authedGet("/health")
        return when (resp.status) {
            HttpStatusCode.OK -> resp.body()
            else -> HealthDto(ok = false, version = null)
        }
    }

    override suspend fun agents(): List<AgentDto> {
        val resp = clientFactory().authedGet("/api/v1/agents")
        if (resp.status != HttpStatusCode.OK) return emptyList()
        return resp.body()
    }

    override suspend fun hands(): List<HandDto> {
        val resp = clientFactory().authedGet("/api/v1/hands")
        if (resp.status != HttpStatusCode.OK) return emptyList()
        return resp.body()
    }

    override suspend fun activateHand(id: String): HandDto {
        val resp = clientFactory().authedPost("/api/v1/hands/$id/activate", Unit)
        return if (resp.status == HttpStatusCode.OK) resp.body()
        else HandDto(id = id, name = id, status = "idle")
    }

    override suspend fun pauseHand(id: String): HandDto {
        val resp = clientFactory().authedPost("/api/v1/hands/$id/pause", Unit)
        return if (resp.status == HttpStatusCode.OK) resp.body()
        else HandDto(id = id, name = id, status = "paused")
    }

    override fun chat(agent: String, userMessage: String): Flow<ChatChunk> = flow {
        val client = clientFactory()
        val body = json.encodeToString(
            ChatRequest.serializer(),
            ChatRequest(
                model = agent,
                messages = listOf(ChatMsg("user", userMessage)),
                stream = true,
            ),
        )
        val resp = client.post(base + "/api/v1/chat") {
            contentType(ContentType.Application.Json)
            tokenProvider()?.let { header("Authorization", "Bearer $it") }
            setBody(body)
        }
        when (resp.status) {
            HttpStatusCode.Unauthorized -> {
                emit(ChatChunk.AuthError)
                return@flow
            }
            HttpStatusCode.OK -> {
                val text = resp.body<String>()
                emit(ChatChunk.Done(text))
            }
            else -> {
                emit(ChatChunk.Error(resp.status.description))
            }
        }
    }

    override fun events(): Flow<OpenFangEvent> = flow {
        // SSE/WS push is wired in Task 6 (DashboardVm subscribes here).
        // For now, emit a single connected event so the flow is
        // non-empty and testable.
        emit(OpenFangEvent(type = "connected", ts = System.currentTimeMillis()))
    }
}

/**
 * In-memory test double.
 */
class MockOpenFangApi(
    private val health: HealthDto = HealthDto(ok = true, version = "0.6.9"),
    private val agents: List<AgentDto> = emptyList(),
    private val hands: List<HandDto> = emptyList(),
) : OpenFangApi {

    override suspend fun health() = health
    override suspend fun agents() = agents
    override suspend fun hands() = hands
    override suspend fun activateHand(id: String) =
        hands.find { it.id == id }?.copy(status = "active") ?: HandDto(id, id, "active")
    override suspend fun pauseHand(id: String) =
        hands.find { it.id == id }?.copy(status = "paused") ?: HandDto(id, id, "paused")
    override fun chat(agent: String, userMessage: String): Flow<ChatChunk> =
        flow {
            emit(ChatChunk.Delta("echo: "))
            emit(ChatChunk.Delta(userMessage))
            emit(ChatChunk.Done("echo: $userMessage"))
        }
    override fun events(): Flow<OpenFangEvent> =
        flow { emit(OpenFangEvent(type = "test", ts = System.currentTimeMillis())) }
}
