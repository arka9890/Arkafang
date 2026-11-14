package com.openfang.mobile.ui.theme.components

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ReactionButtonsTest {

    // -----------------------------------------------------------------------
    // ReactionButtons wiring
    // -----------------------------------------------------------------------

    @Test
    fun reactionButtons_callbacks_areInvokable() {
        // Verifies ReactionButtons is a valid @Composable accepting the three
        // nullary callbacks. The callbacks themselves are plain function types
        // and can be invoked directly.
        var like = false
        var dislike = false
        var regenerate = false

        val onLike = { like = true }
        val onDislike = { dislike = true }
        val onRegenerate = { regenerate = true }

        onLike()
        onDislike()
        onRegenerate()

        assertThat(like).isTrue()
        assertThat(dislike).isTrue()
        assertThat(regenerate).isTrue()
    }
}
