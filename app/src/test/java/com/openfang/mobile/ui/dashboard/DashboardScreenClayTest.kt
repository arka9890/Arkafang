package com.openfang.mobile.ui.dashboard

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DashboardScreenClayTest {

    // -----------------------------------------------------------------------
    // Daemon status text (pure helper used by DashboardScreenClay header)
    // -----------------------------------------------------------------------

    @Test
    fun daemonStatusText_running_showsRunningWithVersion() {
        assertThat(daemonStatusText(daemonRunning = true, daemonVersion = "1.2.3"))
            .isEqualTo("● Running (1.2.3)")
    }

    @Test
    fun daemonStatusText_stopped_showsStopped() {
        assertThat(daemonStatusText(daemonRunning = false, daemonVersion = "unknown"))
            .isEqualTo("○ Stopped")
    }

    @Test
    fun daemonStatusText_running_emptyVersion_stillShowsRunning() {
        assertThat(daemonStatusText(daemonRunning = true, daemonVersion = "stopped"))
            .isEqualTo("● Running (stopped)")
    }
}
