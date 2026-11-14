package com.openfang.mobile.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Drawer width as a fraction of the screen width. */
val SidebarDrawerWidthFraction = 0.84f

/** Drawer maximum width, in dp. */
val SidebarDrawerMaxWidthDp = 340f

/**
 * A slide-left drawer with a close button and a content slot.
 *
 * Animates open/close with [AnimatedVisibility]. The drawer occupies
 * [SidebarDrawerWidthFraction] of the screen width, capped at
 * [SidebarDrawerMaxWidthDp].
 *
 * @param isOpen whether the drawer is visible
 * @param onClose callback invoked to close the drawer
 * @param content the drawer content slot
 * @param modifier modifier applied to the drawer surface
 */
@Composable
fun SidebarDrawer(
    isOpen: Boolean,
    onClose: () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = isOpen,
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(SidebarDrawerWidthFraction)
                .widthIn(max = SidebarDrawerMaxWidthDp.dp)
                .fillMaxHeight(),
            color = MaterialTheme.colorScheme.surface,
            shape = Rounded3XL,
            content = {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Text(
                            text = "\u2716",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(12.dp)
                                .clickable { onClose() },
                        )
                    }
                    content()
                }
            },
        )
    }
}
