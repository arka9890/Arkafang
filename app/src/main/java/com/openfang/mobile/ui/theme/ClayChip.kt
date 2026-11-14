package com.openfang.mobile.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A Pastel Clay chip: a pill-shaped filter/prompt chip.
 *
 * - Shape: pill ([PillShape])
 * - Fill: primaryContainer when selected, surfaceContainerLow otherwise
 * - Text: onPrimaryContainer / onSurface respectively
 * - Shadow: [Modifier.clayElevation], compressed ([Modifier.clayPressed]) while pressed
 *
 * @param label chip label
 * @param selected whether the chip is selected
 * @param onClick click callback
 * @param modifier modifier to apply to the chip
 */
@Composable
fun ClayChip(
    label: String,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow
    val contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface

    Surface(
        modifier = modifier
            .then(if (isPressed) Modifier.clayPressed() else Modifier.clayElevation())
            .clickable(interactionSource = interactionSource, indication = null, enabled = onClick != null, onClick = { if (onClick != null) onClick() }),
        color = containerColor,
        shape = PillShape,
        content = {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge.copy(color = contentColor),
                )
            }
        },
    )
}
