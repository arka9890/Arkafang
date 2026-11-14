package com.openfang.api

import kotlinx.coroutines.flow.Flow

/**
 * Interface for the OpenFang API client. Implementations:
 *  - [OpenFangApiClient] (real HTTP/WS)
 *  - [MockOpenFangApi] (test)
 *
 * All functions are `suspend` or return `Flow` for streaming.
 */
interface OpenFangApi {

    /** Liveness check. */
    suspend fun health(): HealthDto

    /** List all agents. */
    suspend fun agents(): List<AgentDto>

    /** List all 7 Hands with live status. */
    suspend fun hands(): List<HandDto>

    /** Activate a Hand. */
    suspend fun activateHand(id: String): HandDto

    /** Pause a Hand. */
    suspend fun pauseHand(id: String): HandDto

    /**
     * Stream a chat with [agent]. Each [ChatChunk] is emitted as it
     * arrives (delta → delta → … → done). A 401 from the API emits
     * [ChatChunk.AuthError] and stops the stream.
     */
    fun chat(agent: String, userMessage: String): Flow<ChatChunk>

    /**
     * Subscribe to the OpenFang event stream (hand status changes,
     * log lines, etc.).
     */
    fun events(): Flow<OpenFangEvent>
}
