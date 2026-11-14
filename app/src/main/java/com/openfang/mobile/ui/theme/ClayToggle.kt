package com.openfang.mobile.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A Pastel Clay toggle: a pill switch with a clay thumb.
 *
 * - Shape: pill ([PillShape])
 * - Track fill: primary when checked, surfaceContainerLow otherwise
 * - Thumb: circular clay pill positioned at the checked end of the track
 * - Shadow: compressed ([Modifier.clayPressed]) while pressed
 *
 * @param checked whether the toggle is on
 * @param onCheckedChange called with the new value when toggled
 * @param modifier modifier to apply to the toggle
 */
@Composable
fun ClayToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val trackColor = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerLow
    val thumbColor = if (checked) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Surface(
        modifier = modifier
            .size(56.dp, 32.dp)
            .then(if (isPressed) Modifier.clayPressed() else Modifier.clayElevation())
            .clickable(interactionSource = interactionSource, indication = null, onClick = { onCheckedChange(!checked) }),
        color = trackColor,
        shape = PillShape,
        content = {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (checked) Arrangement.End else Arrangement.Start,
            ) {
                Surface(
                    modifier = Modifier.size(24.dp),
                    color = thumbColor,
                    shape = CircleShape,
                    content = {},
                )
            }
        },
    )
}
