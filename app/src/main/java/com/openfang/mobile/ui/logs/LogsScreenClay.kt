package com.openfang.mobile.ui.logs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openfang.mobile.ui.LogsVm
import com.openfang.mobile.ui.theme.ClayCard
import com.openfang.mobile.ui.theme.ClayChip
import com.openfang.mobile.ui.theme.FloatingNavDock
import com.openfang.mobile.ui.theme.PastelClayFontFamily

// ---------------------------------------------------------------------------
// Pure helpers (testable without Compose)
// ---------------------------------------------------------------------------

/**
 * Filters log tail lines by [filterLevel].
 *
 * - "error": keeps lines containing ERROR, FATAL, or Exception.
 * - anything else ("all", "info"): returns the tail unchanged.
 *
 * Mirrors the filtering behaviour of the original [com.openfang.mobile.ui.logs.LogsScreen].
 */
fun filterLogLines(tail: String, filterLevel: String): String =
    when (filterLevel) {
        "error" -> tail.lines().filter {
            it.contains("ERROR", ignoreCase = true) ||
                it.contains("FATAL", ignoreCase = true) ||
                it.contains("Exception")
        }.joinToString("\n")
        else -> tail
    }

/**
 * Auto-refresh indicator text shown above the log card.
 */
fun autoRefreshIndicator(intervalSeconds: Long): String =
    "Auto-refresh every ${intervalSeconds} s"

// ---------------------------------------------------------------------------
// Screen
// ---------------------------------------------------------------------------

/**
 * The logs screen in the Pastel Clay design system.
 *
 * Replaces [LogsScreen]. Renders:
 *  - Filter chips (All / Errors) as a segmented pill control.
 *  - Auto-refresh indicator.
 *  - Tail log file line by line inside a [ClayCard].
 *  - Bottom: [FloatingNavDock].
 *
 * @param vm the [LogsVm] (obtained via [hiltViewModel] by default).
 */
@Composable
fun LogsScreenClay(vm: LogsVm = hiltViewModel()) {
    val st by vm.state.collectAsState()
    val selectedTab = remember { mutableStateOf(0) }

    val isErrorFilter = st.filterLevel == "error"

    Column(modifier = Modifier.fillMaxSize()) {
        // -------------------------------------------------------------------
        // Filter chips: segmented pill control (All / Errors)
        // -------------------------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ClayChip(
                label = "All",
                selected = !isErrorFilter,
                onClick = { vm.setFilter("all") },
            )
            ClayChip(
                label = "Errors",
                selected = isErrorFilter,
                onClick = { vm.setFilter("error") },
            )
        }

        // -------------------------------------------------------------------
        // Auto-refresh indicator
        // -------------------------------------------------------------------
        Text(
            text = autoRefreshIndicator(2),
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = PastelClayFontFamily,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        // -------------------------------------------------------------------
        // Log tail inside a clay card, rendered line by line
        // -------------------------------------------------------------------
        ClayCard(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            val filtered = filterLogLines(st.tail, st.filterLevel)
            Text(
                text = filtered.ifEmpty { "(no logs)" },
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = PastelClayFontFamily,
                    fontSize = 11.sp,
                ),
                modifier = Modifier.fillMaxSize(),
            )
        }

        // -------------------------------------------------------------------
        // Bottom: Floating nav dock
        // -------------------------------------------------------------------
        FloatingNavDock(
            selectedTab = selectedTab.value,
            onTabClick = { selectedTab.value = it },
        )
    }
}
