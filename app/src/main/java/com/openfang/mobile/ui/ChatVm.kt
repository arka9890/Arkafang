package com.openfang.mobile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openfang.api.ChatChunk
import com.openfang.api.OpenFangApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * One turn of a chat conversation.
 */
data class ChatMessage(
    val role: String, // "user" | "assistant"
    val text: String,
    val isStreaming: Boolean = false,
)

/**
 * UI state for the chat screen.
 */
data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isBusy: Boolean = false,
    val authError: Boolean = false,
)

/**
 * Drives the chat UI. Subscribes to [OpenFangApi.chat] and accumulates
 * [ChatChunk]s into [ChatUiState].
 *
 * Review Focus #5 is pinned here: when [ChatChunk.AuthError] arrives,
 * [ChatUiState.authError] becomes true (UI shows a banner + logout /
 * re-auth prompt) instead of silently dropping the message.
 */
class ChatVm(
    private val api: OpenFangApi,
    private val scope: CoroutineScope,
    private val agentId: String = "assistant",
) : ViewModel() {

    private val _state = MutableStateFlow(ChatUiState())
    val state: StateFlow<ChatUiState> = _state.asStateFlow()

    fun send(userMessage: String) {
        if (userMessage.isBlank()) return
        _state.update {
            it.copy(
                messages = it.messages + ChatMessage(role = "user", text = userMessage),
                isBusy = true,
                authError = false,
            )
        }
        var partial = StringBuilder()
        scope.launch {
            try {
                api.chat(agentId, userMessage).collect { chunk ->
                    when (chunk) {
                        is ChatChunk.Delta -> {
                            partial.append(chunk.text)
                            _state.update { s ->
                                s.copy(messages = s.messages + ChatMessage(
                                    role = "assistant",
                                    text = partial.toString(),
                                    isStreaming = true,
                                ))
                            }
                        }
                        is ChatChunk.Done -> {
                            _state.update { s ->
                                val msgs = s.messages.dropLastWhile { it.role == "assistant" && it.isStreaming }
                                s.copy(
                                    messages = msgs + ChatMessage(role = "assistant", text = chunk.fullText),
                                    isBusy = false,
                                )
                            }
                        }
                        is ChatChunk.AuthError -> {
                            _state.update { it.copy(isBusy = false, authError = true) }
                        }
                        is ChatChunk.Error -> {
                            _state.update { s ->
                                s.copy(
                                    messages = s.messages + ChatMessage(role = "system", text = "⚠ ${chunk.message}"),
                                    isBusy = false,
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update { s ->
                    s.copy(
                        messages = s.messages + ChatMessage(role = "system", text = "⚠ ${e.message}"),
                        isBusy = false,
                    )
                }
            }
        }
    }

    /**
     * Clear the "auth error" banner after the user has re-authed.
     */
    fun clearAuthError() {
        _state.update { it.copy(authError = false) }
    }

    /**
     * Remove the streaming assistant message (used when the user
     * cancels mid-stream).
     */
    fun cancelStreaming() {
        _state.update { s -> s.copy(isBusy = false, messages = s.messages.dropLastWhile { it.isStreaming }) }
    }
}
