package com.openfang.mobile.ui.logs

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LogsScreenClayTest {

    // -----------------------------------------------------------------------
    // filterLogLines (pure helper used by LogsScreenClay)
    // -----------------------------------------------------------------------

    @Test
    fun filterLogLines_allFilter_returnsAllLines() {
        val tail = "INFO started\nERROR something failed\nWARN be careful"
        assertThat(filterLogLines(tail, "all")).isEqualTo(tail)
    }

    @Test
    fun filterLogLines_infoFilter_returnsAllLines() {
        val tail = "INFO started\nERROR something failed\nWARN be careful"
        assertThat(filterLogLines(tail, "info")).isEqualTo(tail)
    }

    @Test
    fun filterLogLines_errorFilter_returnsOnlyErrorLines() {
        val tail = "INFO started\nERROR something failed\nWARN be careful\nFATAL crash"
        assertThat(filterLogLines(tail, "error"))
            .isEqualTo("ERROR something failed\nFATAL crash")
    }

    @Test
    fun filterLogLines_errorFilter_caseInsensitiveAndException() {
        val tail = "error lowercase\nError mixed\nFATAL uppercase\noops Exception here\nINFO all good"
        assertThat(filterLogLines(tail, "error"))
            .isEqualTo("error lowercase\nError mixed\nFATAL uppercase\noops Exception here")
    }

    @Test
    fun filterLogLines_emptyTail_returnsEmpty() {
        assertThat(filterLogLines("", "error")).isEqualTo("")
    }

    // -----------------------------------------------------------------------
    // autoRefreshIndicator (pure helper used by LogsScreenClay)
    // -----------------------------------------------------------------------

    @Test
    fun autoRefreshIndicator_showsInterval() {
        assertThat(autoRefreshIndicator(2)).isEqualTo("Auto-refresh every 2 s")
    }

    @Test
    fun autoRefreshIndicator_customInterval() {
        assertThat(autoRefreshIndicator(5)).isEqualTo("Auto-refresh every 5 s")
    }
}
