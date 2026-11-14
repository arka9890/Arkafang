package com.openfang.mobile.ui.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openfang.mobile.ui.SetupVm
import com.openfang.mobile.ui.theme.ClayButton
import com.openfang.mobile.ui.theme.ClayCard
import com.openfang.mobile.ui.theme.FloatingNavDock
import com.openfang.mobile.ui.theme.PastelClayFontFamily

// ---------------------------------------------------------------------------
// Pure helpers (testable without Compose)
// ---------------------------------------------------------------------------

/**
 * Human-readable label for a setup phase (Indonesian).
 *
 * Mirrors the one-time flow: download → unpack → verify → smoke test → done.
 */
fun setupPhaseLabel(phase: SetupVm.Phase): String = when (phase) {
    SetupVm.Phase.Idle -> "Siap memulai setup"
    is SetupVm.Phase.Downloading -> "Mengunduh ${phase.fileName}…"
    SetupVm.Phase.Unpacking -> "Mengekstrak lingkungan…"
    SetupVm.Phase.Verifying -> "Memverifikasi checksum + smoke test…"
    SetupVm.Phase.Done -> "Setup selesai"
    is SetupVm.Phase.Failed -> "Setup gagal: ${phase.reason}"
}

/**
 * Progress (0–1) across the setup phases.
 *
 * Each phase occupies an equal band so the clay progress bar advances
 * deterministically through the flow regardless of byte counts.
 */
fun setupPhaseProgress(phase: SetupVm.Phase): Float = when (phase) {
    SetupVm.Phase.Idle -> 0f
    is SetupVm.Phase.Downloading -> 0.25f
    SetupVm.Phase.Unpacking -> 0.5f
    SetupVm.Phase.Verifying -> 0.75f
    SetupVm.Phase.Done -> 1f
    is SetupVm.Phase.Failed -> 0f
}

// ---------------------------------------------------------------------------
// Clay progress bar
// ---------------------------------------------------------------------------

/**
 * A clay-styled linear progress bar.
 *
 * @param progress fraction complete (0–1), clamped internally.
 * @param modifier modifier to apply to the bar.
 */
@Composable
fun ClayProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val clamped = progress.coerceIn(0f, 1f)
    Row(
        modifier = modifier
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Filled portion.
        Box(
            modifier = Modifier
                .weight(clamped)
                .background(MaterialTheme.colorScheme.primary),
        )
        // Remaining track (omitted when complete).
        if (clamped < 1f) {
            Box(
                modifier = Modifier
                    .weight(1f - clamped)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Screen
// ---------------------------------------------------------------------------

/**
 * The one-time setup screen in the Pastel Clay design system.
 *
 * Replaces [SetupScreen]. Renders:
 *  - Title and phase label.
 *  - Clay progress bar (download → unpack → verify → smoke test).
 *  - Retry (clay pill button) on failure; Start button when idle.
 *  - Bottom: [FloatingNavDock] on the "Pengaturan" tab.
 *
 * @param vm the [SetupVm] (obtained via [hiltViewModel] by default).
 */
@Composable
fun SetupScreenClay(vm: SetupVm = hiltViewModel()) {
    val phase by vm.phase.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            text = "Setup OpenFang",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        )

        ClayCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(
                text = setupPhaseLabel(phase),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = PastelClayFontFamily,
                ),
                modifier = Modifier.padding(bottom = 12.dp),
            )

            ClayProgressBar(
                progress = setupPhaseProgress(phase),
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = "${(setupPhaseProgress(phase) * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = PastelClayFontFamily,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                modifier = Modifier.padding(top = 8.dp).align(Alignment.End),
            )
        }

        when (phase) {
            is SetupVm.Phase.Failed -> {
                ClayButton(
                    onClick = { vm.retry(); vm.runSetup() },
                    content = { Text(text = "Coba lagi") },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    enabled = true,
                )
            }
            SetupVm.Phase.Idle -> {
                ClayButton(
                    onClick = { vm.runSetup() },
                    content = { Text(text = "Mulai setup") },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    enabled = true,
                )
            }
            else -> Unit
        }

        // Push the dock to the bottom of the screen.
        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.weight(1f))

        FloatingNavDock(
            selectedTab = 2,
            onTabClick = { },
        )
    }
}