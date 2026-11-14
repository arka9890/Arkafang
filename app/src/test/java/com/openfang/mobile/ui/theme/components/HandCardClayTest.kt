package com.openfang.mobile.ui.theme.components

import androidx.compose.ui.graphics.Color
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class HandCardClayTest {

    // -----------------------------------------------------------------------
    // Metrics parsing
    // -----------------------------------------------------------------------

    @Test
    fun parseMetricsJson_validJson_returnsKeyValuePairs() {
        val result = parseMetricsJson("{\"tokens\": \"120\", \"latency\": \"45ms\"}")
        assertThat(result).containsExactly("tokens" to "120", "latency" to "45ms").inOrder()
    }

    @Test
    fun parseMetricsJson_emptyObject_returnsEmptyList() {
        assertThat(parseMetricsJson("{}")).isEmpty()
    }

    @Test
    fun parseMetricsJson_invalidJson_returnsEmptyList() {
        assertThat(parseMetricsJson("not-json")).isEmpty()
    }

    @Test
    fun parseMetricsJson_nullishValues_stillParsed() {
        val result = parseMetricsJson("{\"count\": 5, \"rate\": null}")
        assertThat(result).contains("count" to "5")
    }

    // -----------------------------------------------------------------------
    // Status color
    // -----------------------------------------------------------------------

    @Test
    fun statusColor_running_returnsActiveColor() {
        assertThat(statusColor("running", Color(0xFF4361EE), Color(0xFF747686)))
            .isEqualTo(Color(0xFF4361EE))
    }

    @Test
    fun statusColor_active_returnsActiveColor() {
        assertThat(statusColor("ACTIVE", Color(0xFF4361EE), Color(0xFF747686)))
            .isEqualTo(Color(0xFF4361EE))
    }

    @Test
    fun statusColor_stopped_returnsInactiveColor() {
        assertThat(statusColor("stopped", Color(0xFF4361EE), Color(0xFF747686)))
            .isEqualTo(Color(0xFF747686))
    }

    @Test
    fun statusColor_unknown_returnsInactiveColor() {
        assertThat(statusColor("unknown", Color(0xFF4361EE), Color(0xFF747686)))
            .isEqualTo(Color(0xFF747686))
    }

    // -----------------------------------------------------------------------
    // Last-run timestamp formatting
    // -----------------------------------------------------------------------

    @Test
    fun formatLastRun_null_returnsNever() {
        assertThat(formatLastRun(null)).isEqualTo("never")
    }

    @Test
    fun formatLastRun_withTimestamp_returnsFormatted() {
        assertThat(formatLastRun(1720000000000L)).isEqualTo("1720000000000 ms ago")
    }
}
