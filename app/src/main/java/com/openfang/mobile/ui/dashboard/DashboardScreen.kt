package com.openfang.mobile.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openfang.mobile.ui.DashboardVm
import com.openfang.mobile.ui.HandState

/**
 * The main dashboard: daemon status header, 7-Hand grid, agent list,
 * bottom tabs to Chat / Logs.
 */
@Composable
fun DashboardScreen(vm: DashboardVm = hiltViewModel()) {
    val st by vm.state.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp),
    ) {
        item {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val running = st.daemonRunning
                Text(
                    text = if (running) "● Running (${st.daemonVersion})" else "○ Stopped",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (running) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.weight(1f))
                if (running) {
                    OutlinedButton(onClick = { vm.stopDaemon() }) { Text("Stop") }
                } else {
                    Button(onClick = { vm.startDaemon() }) { Text("Start") }
                }
            }
        }

        // Kernel warning banner (Review Focus #3)
        st.kernelWarning?.let { msg ->
            item {
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    tonalElevation = 2.dp,
                ) {
                    Text(
                        text = "⚠ $msg",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        // Load error
        st.loadError?.let { msg ->
            item {
                Surface(color = MaterialTheme.colorScheme.errorContainer) {
                    Text(
                        text = "⚠ $msg",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        // Hands section
        item {
            Text("Hands", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        items(st.hands) { hand ->
            HandCard(hand = hand, onActivate = { vm.activateHand(hand.id) }, onPause = { vm.pauseHand(hand.id) }, vm = vm)
        }

        // Agents section
        item {
            Text("Agents", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        items(st.agents) { agent ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(agent.name, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.weight(1f))
                    Text(
                        agent.status,
                        style = MaterialTheme.typography.labelMedium,
                        color = when (agent.status) {
                            "running" -> MaterialTheme.colorScheme.primary
                            "error" -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun HandCard(hand: HandState, onActivate: () -> Unit, onPause: () -> Unit, vm: DashboardVm) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(hand.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                StatusPill(status = hand.status)
            }
            vm.parseMetrics(hand.metrics).forEach { (k, v) ->
                Text("$k: $v", style = MaterialTheme.typography.labelSmall)
            }
            if (hand.lastRunAt != null) {
                Text("Last: ${java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.US).format(java.util.Date(hand.lastRunAt))}", style = MaterialTheme.typography.labelSmall)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                when (hand.status) {
                    "active" -> OutlinedButton(onClick = onPause) { Text("Pause") }
                    "paused" -> Button(onClick = onActivate) { Text("Activate") }
                    else -> Button(onClick = onActivate) { Text("Activate") }
                }
            }
        }
    }
}

@Composable
fun StatusPill(status: String) {
    val color = when (status) {
        "active" -> MaterialTheme.colorScheme.primary
        "paused" -> MaterialTheme.colorScheme.tertiary
        "error" -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(color = color.copy(alpha = 0.15f), shape = MaterialTheme.shapes.small) {
        Text(
            text = status.uppercase(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
        )
    }
}
