package com.openfang.api

import kotlinx.serialization.Serializable

/**
 * DTOs for the OpenFang REST + WebSocket API.
 *
 * These match the response shapes in OpenFang v0.6.9's
 * `openfang-api` crate. If a field is added upstream, the deserializer
 * must be lenient (we use `ignoreUnknownKeys = true`).
 *
 * [HandDto.metrics] is a `String` (JSON-encoded) instead of
 * `Map<String, Any>` — the UI layer parses it via [JsonElement] when
 * rendering.
 */
@Serializable
data class HealthDto(
    val ok: Boolean = false,
    val version: String? = null,
)

@Serializable
data class AgentDto(
    val id: String,
    val name: String,
    val status: String = "idle",  // idle | running | error
)

@Serializable
data class HandDto(
    val id: String,
    val name: String,
    val status: String = "idle",  // active | paused | idle
    val lastRunAt: Long? = null,  // unix ms
    val metrics: String = "{}",    // JSON-encoded map; parsed at render time
)

@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<ChatMsg>,
    val stream: Boolean = true,
)

@Serializable
data class ChatMsg(
    val role: String,
    val content: String,
)

/**
 * A single chat chunk (SSE / WS). The app streams these into the
 * chat UI. [AuthError] is a synthetic chunk the client emits when
 * the API returns 401 — pins Review Focus #5.
 */
sealed class ChatChunk {
    data class Delta(val text: String) : ChatChunk()
    data class Done(val fullText: String) : ChatChunk()
    data class Error(val message: String) : ChatChunk()
    object AuthError : ChatChunk()
}

/**
 * A generic event from the OpenFang WS stream.
 */
@Serializable
data class OpenFangEvent(
    val type: String,
    val payload: String = "{}",  // JSON-encoded
    val ts: Long = 0,
)
