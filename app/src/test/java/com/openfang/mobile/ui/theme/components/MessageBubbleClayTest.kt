package com.openfang.mobile.ui.theme.components

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MessageBubbleClayTest {

    // -----------------------------------------------------------------------
    // User bubble — gradient primary-to-light
    // -----------------------------------------------------------------------

    @Test
    fun userBubbleGradient_returnsBrush() {
        assertThat(userBubbleGradient(Color(0xFF4361EE), Color(0xFFF4F2FF)))
            .isInstanceOf(Brush::class.java)
    }

    @Test
    fun messageBubbleShape_user_returnsShape() {
        assertThat(messageBubbleShape(isUser = true)).isInstanceOf(Shape::class.java)
    }

    @Test
    fun messageBubbleShape_ai_returnsShape() {
        assertThat(messageBubbleShape(isUser = false)).isInstanceOf(Shape::class.java)
    }

    // -----------------------------------------------------------------------
    // AI bubble — white clay surface
    // -----------------------------------------------------------------------

    @Test
    fun aiBubbleColor_returnsSurfaceColor() {
        assertThat(aiBubbleColor(Color(0xFFFBF8FF))).isEqualTo(Color(0xFFFBF8FF))
    }

    // -----------------------------------------------------------------------
    // System bubble — error container
    // -----------------------------------------------------------------------

    @Test
    fun systemBubbleColor_returnsErrorContainer() {
        assertThat(systemBubbleColor(Color(0xFFFFDAD6))).isEqualTo(Color(0xFFFFDAD6))
    }
}
