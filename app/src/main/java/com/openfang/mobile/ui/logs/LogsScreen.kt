package com.openfang.mobile.ui.logs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openfang.mobile.ui.LogsVm

/**
 * Tails the OpenFang log file. Two tabs: "all" / "error".
 * Auto-refreshes every 2 s (driven by [LogsVm]).
 */
@Composable
fun LogsScreen(vm: LogsVm = hiltViewModel()) {
    val st by vm.state.collectAsState()
    val tabIndexState = remember { mutableIntStateOf(0) }

    Column(Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tabIndexState.value) {
            Tab(
                selected = tabIndexState.value == 0,
                onClick = { tabIndexState.value = 0; vm.setFilter("all") },
            ) {
                Text("All")
            }
            Tab(
                selected = tabIndexState.value == 1,
                onClick = { tabIndexState.value = 1; vm.setFilter("error") },
            ) {
                Text("Errors")
            }
        }
        val filtered = when (st.filterLevel) {
            "error" -> st.tail.lines().filter {
                it.contains("ERROR", ignoreCase = true) ||
                    it.contains("FATAL", ignoreCase = true) ||
                    it.contains("Exception")
            }.joinToString("\n")
            else -> st.tail
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
        ) {
            Text(
                text = filtered.ifEmpty { "(no logs)" },
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                ),
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
