package com.openfang.mobile.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openfang.api.AgentDto
import com.openfang.mobile.ui.DashboardVm
import com.openfang.mobile.ui.theme.ClayButton
import com.openfang.mobile.ui.theme.ClayCard
import com.openfang.mobile.ui.theme.FloatingNavDock
import com.openfang.mobile.ui.theme.components.HandCardClay

/**
 * Pure helper: daemon status display text used by the header.
 *
 * Returns "● Running (<version>)" when the daemon is running, otherwise
 * "○ Stopped". Extracted so it can be unit-tested without Compose.
 */
fun daemonStatusText(daemonRunning: Boolean, daemonVersion: String): String =
    if (daemonRunning) "● Running ($daemonVersion)" else "○ Stopped"

/**
 * The main dashboard screen in the Pastel Clay design system.
 *
 * Replaces [DashboardScreen]. Renders:
 *  - Header: daemon status (Running/Stopped) inside a [ClayCard] with Start/Stop [ClayButton]s.
 *  - Kernel warning banner (when [DashboardUiState.kernelWarning] is set).
 *  - Hands section: a [HandCardClay] per hand.
 *  - Agents section: a [ClayCard] per agent.
 *  - Bottom: [FloatingNavDock].
 *
 * @param vm the [DashboardVm] (obtained via [hiltViewModel] by default).
 */
@Composable
fun DashboardScreenClay(vm: DashboardVm = hiltViewModel()) {
    val st by vm.state.collectAsState()
    val selectedTab = remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(16.dp),
        ) {
            // -----------------------------------------------------------------
            // Header: daemon status inside a clay card
            // -----------------------------------------------------------------
            item {
                ClayCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = daemonStatusText(st.daemonRunning, st.daemonVersion),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (st.daemonRunning) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.weight(1f))
                        if (st.daemonRunning) {
                            ClayButton(
                                onClick = { vm.stopDaemon() },
                                content = { Text(text = "Stop") },
                            )
                        } else {
                            ClayButton(
                                onClick = { vm.startDaemon() },
                                content = { Text(text = "Start") },
                            )
                        }
                    }
                }
            }

            // -----------------------------------------------------------------
            // Kernel warning banner (Review Focus #3)
            // -----------------------------------------------------------------
            st.kernelWarning?.let { msg ->
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        tonalElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "⚠ $msg",
                            modifier = Modifier.fillMaxWidth().padding(paddingValues = PaddingValues(12.dp)),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }

            // -----------------------------------------------------------------
            // Load error
            // -----------------------------------------------------------------
            st.loadError?.let { msg ->
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "⚠ $msg",
                            modifier = Modifier.fillMaxWidth().padding(paddingValues = PaddingValues(12.dp)),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }

            // -----------------------------------------------------------------
            // Hands section: HandCardClay per hand
            // -----------------------------------------------------------------
            item {
                Text(
                    text = "Hands",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
            items(st.hands) { hand ->
                HandCardClay(
                    hand = hand,
                    onActivate = { vm.activateHand(hand.id) },
                    onPause = { vm.pauseHand(hand.id) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // -----------------------------------------------------------------
            // Agents section: clay card per agent
            // -----------------------------------------------------------------
            item {
                Text(
                    text = "Agents",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
            items(st.agents) { agent: AgentDto ->
                ClayCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(paddingValues = PaddingValues(horizontal = 12.dp, vertical = 4.dp)),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = agent.name,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = agent.status,
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

        // -----------------------------------------------------------------
        // Bottom: Floating nav dock
        // -----------------------------------------------------------------
        FloatingNavDock(selectedTab = selectedTab.value, onTabClick = { selectedTab.value = it })
    }
}
