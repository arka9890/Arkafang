package com.openfang.mobile.ui.theme

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ModelSelectorTest {

    // -----------------------------------------------------------------------
    // Spec values ModelSelector consumes / exposes
    // -----------------------------------------------------------------------

    @Test
    fun availableModels_hasFourModels() {
        assertThat(AvailableModels).hasSize(4)
    }

    @Test
    fun availableModels_containsExpectedModels() {
        assertThat(AvailableModels.map { it.name })
            .containsExactly(
                "Claude 3.5 Sonnet",
                "GPT-4o",
                "Gemini 1.5 Pro",
                "Llama 3.3 70B",
            )
    }
}
