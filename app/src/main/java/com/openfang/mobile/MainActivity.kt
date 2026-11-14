package com.openfang.mobile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.openfang.mobile.ui.chat.ChatScreenClay
import com.openfang.mobile.ui.dashboard.DashboardScreenClay
import com.openfang.mobile.ui.logs.LogsScreenClay
import com.openfang.mobile.ui.setup.SetupScreenClay
import com.openfang.mobile.ui.theme.FloatingNavDock
import com.openfang.mobile.ui.theme.PastelClayTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-activity app. The Compose tree decides which screen to show via a
 * 4-value [selectedTab] index (Chat / Provider / Pengaturan / Logs).
 *
 * 4-screen vs 3-tab mapping decision:
 *   The [FloatingNavDock] (Task 3) exposes exactly three tabs — Chat, Provider,
 *   Pengaturan — and its unit test pins that list to size 3, so we keep it
 *   unchanged. We therefore consolidate the four Clay screens onto the three
 *   dock tabs and expose the fourth (Logs) through the header's "Logs" chip:
 *
 *     dock tab 0 (Chat)       -> ChatScreenClay
 *     dock tab 1 (Provider)   -> DashboardScreenClay
 *     dock tab 2 (Pengaturan) -> SetupScreenClay
 *     header "Logs" chip      -> LogsScreenClay (selectedTab = 3)
 *
 * The dock's `selectedTab` is clamped to 0..2 so the three-tab dock always
 * highlights a valid tab even while the Logs screen (index 3) is showing.
 *
 * Dark mode preference is persisted in [SharedPreferences] and applied
 * when MainActivity opens, wrapped in [PastelClayTheme].
 *
 * No BOOT_COMPLETED receiver — start is manual (spec §3, §6.6).
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        private const val PREFS_NAME = "openfang_theme_prefs"
        private const val KEY_IS_DARK = "is_dark_mode"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isDark = loadDarkModePref(this)
        setContent {
            PastelClayTheme(isDark = isDark) {
                ClayApp(
                    isDark = isDark,
                    onThemeToggle = { saveDarkModePref(this@MainActivity, it) },
                )
            }
        }
    }

    private fun loadDarkModePref(context: Context): Boolean {
        val prefs: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_DARK, false)
    }

    private fun saveDarkModePref(context: Context, isDark: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_IS_DARK, isDark)
            .apply()
    }
}

@Composable
private fun ClayApp(
    isDark: Boolean,
    onThemeToggle: (Boolean) -> Unit,
) {
    val selectedTabState = remember { mutableStateOf(0) }
    val darkModeState = remember { mutableStateOf(isDark) }
    val selectedTab = selectedTabState.value
    val darkMode = darkModeState.value

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        // Header: title + "Logs" chip (4th screen) + dark mode toggle.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "OpenFang",
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.weight(1f))
            // 4th screen (Logs) is not a dock tab, so expose it as a chip here.
            val onLogs = selectedTab == 3
            Text(
                text = if (onLogs) "Logs ✓" else "Logs",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable { selectedTabState.value = if (onLogs) 0 else 3 }
                    .padding(horizontal = 4.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Dark mode",
                style = MaterialTheme.typography.bodyMedium,
            )
            Switch(
                checked = darkMode,
                onCheckedChange = {
                    darkModeState.value = it
                    onThemeToggle(it)
                },
            )
        }

        // Screen content — 4 Clay screens selected by selectedTab.
        //   0 Chat       -> ChatScreenClay
        //   1 Provider   -> DashboardScreenClay
        //   2 Pengaturan -> SetupScreenClay
        //   3 Logs       -> LogsScreenClay (reached via the header chip)
        when (selectedTab) {
            0 -> ChatScreenClay()
            1 -> DashboardScreenClay()
            2 -> SetupScreenClay()
            3 -> LogsScreenClay()
            else -> ChatScreenClay()
        }

        // Bottom: floating nav dock. Clamped to the dock's 3 tabs so the
        // Logs screen (index 3) still highlights a valid tab.
        FloatingNavDock(
            selectedTab = selectedTab.coerceIn(0, 2),
            onTabClick = { selectedTabState.value = it },
        )
    }
}
