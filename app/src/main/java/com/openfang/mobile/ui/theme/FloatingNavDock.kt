package com.openfang.mobile.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Labels for the three dock tabs. */
val FloatingNavTabs = listOf("Chat", "Provider", "Pengaturan")

/** Backdrop blur color for the floating dock: rgba(255, 255, 255, 0.85). */
val FloatingNavDockBackdrop = Color(0xD9FFFFFF)

/** Inactive tab text color (slate-500). */
val FloatingNavTabInactive = Color(0xFF64748B)

/**
 * A bottom pill navigation bar with three tabs: Chat, Provider, Pengaturan.
 *
 * - Active tab: primary text on a white clay pill with [Modifier.clayElevation]
 * - Inactive tab: slate-500 text on a transparent background
 *
 * @param selectedTab index of the active tab
 * @param onTabClick callback invoked with the tapped tab index
 * @param modifier modifier applied to the dock
 */
@Composable
fun FloatingNavDock(
    selectedTab: Int,
    onTabClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FloatingNavTabs.forEachIndexed { index, label ->
            val isSelected = index == selectedTab
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onTabClick(index) }
                    .then(if (isSelected) Modifier.clayElevation() else Modifier),
                color = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                shape = PillShape,
                content = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = if (isSelected) MaterialTheme.colorScheme.primary else FloatingNavTabInactive,
                        ),
                    )
                },
            )
        }
    }
}
