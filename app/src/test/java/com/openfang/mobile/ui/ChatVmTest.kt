package com.openfang.mobile.ui

import com.google.common.truth.Truth.assertThat
import com.openfang.api.ChatChunk
import com.openfang.api.MockOpenFangApi
import com.openfang.api.OpenFangApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.concurrent.atomic.AtomicReference

/**
 * Task 6: ChatVm test.
 *
 * Pins Review Focus #5 at the VM level: when the API emits
 * [ChatChunk.AuthError], the VM sets [ChatUiState.authError] = true
 * instead of silently dropping.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ChatVmTest {

    /** An API whose chat flow emits AuthError immediately. */
    private class AuthErrorApi : OpenFangApi {
        override suspend fun health() = com.openfang.api.HealthDto(ok = false)
        override suspend fun agents() = emptyList()
        override suspend fun hands() = emptyList()
        override suspend fun activateHand(id: String) = com.openfang.api.HandDto(id, id, "idle")
        override suspend fun pauseHand(id: String) = com.openfang.api.HandDto(id, id, "paused")
        override fun chat(agent: String, userMessage: String): Flow<ChatChunk> =
            kotlinx.coroutines.flow.flow { emit(ChatChunk.AuthError) }
        override fun events(): Flow<com.openfang.api.OpenFangEvent> =
            kotlinx.coroutines.flow.flow {}
    }

    @Test
    fun testAuthErrorSetsFlag() = runTest {
        val scope = TestScope()
        val vm = ChatVm(api = AuthErrorApi(), scope = scope)
        val initial = vm.state.value
        assertThat(initial.authError).isFalse()

        vm.send("hello")
        // Allow the flow to collect
        val final = vm.state.value
        // The AuthError chunk flips the flag
        assertThat(final.authError).isTrue()
        assertThat(final.isBusy).isFalse()
    }

    @Test
    fun testNormalChatAccumulates() = runTest {
        val api = MockOpenFangApi()
        val scope = TestScope()
        val vm = ChatVm(api = api, scope = scope)

        vm.send("ping")
        val st = vm.state.value
        // After the flow completes:
        //  - user message is in the list
        //  - the streaming assistant message is replaced by a Done one
        val user = st.messages.first { it.role == "user" }
        assertThat(user.text).isEqualTo("ping")
        val assistant = st.messages.filter { it.role == "assistant" }
        assertThat(assistant).isNotEmpty()
        assertThat(assistant.all { !it.isStreaming }).isTrue()
    }
}
