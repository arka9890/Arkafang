package com.openfang.mobile.ui.chat

import com.google.common.truth.Truth.assertThat
import com.openfang.mobile.ui.ChatMessage
import org.junit.Test

class ChatScreenClayTest {

    // -----------------------------------------------------------------------
    // streamingIndicatorVisible (pure helper used by ChatScreenClay message list)
    // -----------------------------------------------------------------------

    @Test
    fun streamingIndicatorVisible_isBusy_showsIndicator() {
        assertThat(streamingIndicatorVisible(isBusy = true, messages = emptyList())).isTrue()
    }

    @Test
    fun streamingIndicatorVisible_notBusyButStreamingMessage_showsIndicator() {
        val messages = listOf(ChatMessage(role = "assistant", text = "halo", isStreaming = true))
        assertThat(streamingIndicatorVisible(isBusy = false, messages = messages)).isTrue()
    }

    @Test
    fun streamingIndicatorVisible_idle_noStreaming_hidesIndicator() {
        val messages = listOf(ChatMessage(role = "assistant", text = "halo", isStreaming = false))
        assertThat(streamingIndicatorVisible(isBusy = false, messages = messages)).isFalse()
    }

    @Test
    fun streamingIndicatorVisible_emptyMessages_notBusy_hidesIndicator() {
        assertThat(streamingIndicatorVisible(isBusy = false, messages = emptyList())).isFalse()
    }

    // -----------------------------------------------------------------------
    // quotaText (pure helper used by the sidebar quota widget)
    // -----------------------------------------------------------------------

    @Test
    fun quotaText_showsUsedAndTotal() {
        assertThat(quotaText(used = 120, total = 500)).isEqualTo("120 / 500 MB")
    }

    @Test
    fun quotaText_zeroUsed_showsZero() {
        assertThat(quotaText(used = 0, total = 500)).isEqualTo("0 / 500 MB")
    }
}
