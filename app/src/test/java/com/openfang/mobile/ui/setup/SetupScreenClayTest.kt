package com.openfang.mobile.ui.setup

import com.google.common.truth.Truth.assertThat
import com.openfang.mobile.ui.SetupVm
import org.junit.Test

class SetupScreenClayTest {

    // -----------------------------------------------------------------------
    // setupPhaseLabel (pure helper used by SetupScreenClay)
    // -----------------------------------------------------------------------

    @Test
    fun setupPhaseLabel_idle_showsReady() {
        assertThat(setupPhaseLabel(SetupVm.Phase.Idle)).isEqualTo("Siap memulai setup")
    }

    @Test
    fun setupPhaseLabel_downloading_includesFileName() {
        assertThat(setupPhaseLabel(SetupVm.Phase.Downloading(0L, "ubuntu-aarch64.tar.xz")))
            .isEqualTo("Mengunduh ubuntu-aarch64.tar.xz…")
    }

    @Test
    fun setupPhaseLabel_unpacking_showsExtracting() {
        assertThat(setupPhaseLabel(SetupVm.Phase.Unpacking)).isEqualTo("Mengekstrak lingkungan…")
    }

    @Test
    fun setupPhaseLabel_verifying_showsVerifying() {
        assertThat(setupPhaseLabel(SetupVm.Phase.Verifying))
            .isEqualTo("Memverifikasi checksum + smoke test…")
    }

    @Test
    fun setupPhaseLabel_done_showsSelesai() {
        assertThat(setupPhaseLabel(SetupVm.Phase.Done)).isEqualTo("Setup selesai")
    }

    @Test
    fun setupPhaseLabel_failed_includesReason() {
        assertThat(setupPhaseLabel(SetupVm.Phase.Failed("checksum mismatch")))
            .isEqualTo("Setup gagal: checksum mismatch")
    }

    // -----------------------------------------------------------------------
    // setupPhaseProgress (pure helper used by SetupScreenClay)
    // -----------------------------------------------------------------------

    @Test
    fun setupPhaseProgress_idle_isZero() {
        assertThat(setupPhaseProgress(SetupVm.Phase.Idle)).isEqualTo(0f)
    }

    @Test
    fun setupPhaseProgress_downloading_isQuarter() {
        assertThat(setupPhaseProgress(SetupVm.Phase.Downloading(0L, "x"))).isEqualTo(0.25f)
    }

    @Test
    fun setupPhaseProgress_unpacking_isHalf() {
        assertThat(setupPhaseProgress(SetupVm.Phase.Unpacking)).isEqualTo(0.5f)
    }

    @Test
    fun setupPhaseProgress_verifying_isThreeQuarters() {
        assertThat(setupPhaseProgress(SetupVm.Phase.Verifying)).isEqualTo(0.75f)
    }

    @Test
    fun setupPhaseProgress_done_isOne() {
        assertThat(setupPhaseProgress(SetupVm.Phase.Done)).isEqualTo(1f)
    }

    @Test
    fun setupPhaseProgress_failed_isZero() {
        assertThat(setupPhaseProgress(SetupVm.Phase.Failed("boom"))).isEqualTo(0f)
    }
}
