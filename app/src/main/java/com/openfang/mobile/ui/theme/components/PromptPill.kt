package com.openfang.mobile.ui.theme.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.openfang.mobile.ui.theme.ClayShadowConfig
import com.openfang.mobile.ui.theme.PillShape
import com.openfang.mobile.ui.theme.clayElevation
import com.openfang.mobile.ui.theme.clayPressed

/**
 * Horizontal prompt suggestion pill.
 *
 * A pill-shaped, tappable chip that suggests a prompt to the user. Mirrors
 * [ClayChip]'s pressed/elevation behavior but is optimized for short prompt
 * labels rather than filter tags.
 *
 * @param label prompt text shown inside the pill
 * @param onClick callback invoked when the pill is tapped
 * @param modifier modifier to apply to the pill
 */
@Composable
fun PromptPill(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Surface(
        modifier = modifier
            .then(if (isPressed) Modifier.clayPressed() else Modifier.clayElevation())
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = true,
                onClick = onClick,
            ),
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = PillShape,
        content = {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
                )
            }
        },
    )
}

/** Content (text) color for a PromptPill, derived from the on-secondary-container role. */
internal fun promptPillTextColor(onSecondaryContainer: androidx.compose.ui.graphics.Color) =
    onSecondaryContainer
