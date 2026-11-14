package com.openfang.mobile.ui.setup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openfang.mobile.ui.SetupVm
import com.openfang.mobile.ui.SetupVm.Phase

/**
 * One-time setup screen. Shown when [com.openfang.mobile.data.SetupStateStore]
 * reports incomplete.
 *
 * Layout:
 *   - title
 *   - progress card (file being downloaded + %)
 *   - phase indicator (download → unpack → verify → done)
 *   - retry button on failure
 */
@Composable
fun SetupScreen(vm: SetupVm = hiltViewModel()) {
    val phase = vm.phase.collectAsState().value
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Set up OpenFang", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        when (phase) {
            Phase.Idle -> {
                Text("First run: download environment (~150 MB)", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(16.dp))
                Button(onClick = { vm.runSetup() }) { Text("Start setup") }
            }
            is Phase.Downloading -> {
                val mb = (phase.bytes / 1024 / 1024).toInt()
                Text("Downloading ${phase.fileName}… ${mb} MB", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text("Mirror: official → GitHub Releases → user", style = MaterialTheme.typography.labelSmall)
            }
            Phase.Unpacking -> {
                Text("Unpacking environment…", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                CircularProgressIndicator()
            }
            Phase.Verifying -> {
                Text("Verifying checksums + smoke test…", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                CircularProgressIndicator()
            }
            Phase.Done -> {
                Text("✅ Setup complete", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(8.dp))
                Text("Tap Start on the dashboard to launch the daemon.", style = MaterialTheme.typography.bodyMedium)
            }
            is Phase.Failed -> {
                Text("Setup failed", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
                Text(phase.reason, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(16.dp))
                OutlinedButton(onClick = { vm.retry(); vm.runSetup() }) { Text("Retry") }
            }
        }
    }
}
