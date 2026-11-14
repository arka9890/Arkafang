package com.openfang.mobile.ui.theme

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A Pastel Clay button: a fully rounded pill button.
 *
 * - Fill: primary (#4361EE)
 * - Text: white (onPrimary)
 * - Shape: pill ([PillShape])
 * - Shadow: [Modifier.clayElevation], compressed ([Modifier.clayPressed]) when active
 *
 * @param onClick click callback
 * @param content button content (typically [Text] or [Icon])
 * @param modifier modifier to apply
 * @param enabled whether the button is enabled
 */
@Composable
fun ClayButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier = modifier.then(if (isPressed) Modifier.clayPressed() else Modifier.clayElevation()),
        shape = PillShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        content = { content() },
    )
}
