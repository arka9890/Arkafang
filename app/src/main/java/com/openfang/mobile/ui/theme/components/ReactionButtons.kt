package com.openfang.mobile.ui.theme.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Reaction buttons: like / dislike / regenerate.
 *
 * A horizontal row of pill buttons rendered beneath a chat message bubble.
 * Each button exposes a nullary callback so the caller (e.g. a screen or
 * ViewModel) can wire up the actual reaction logic.
 *
 * @param onLike callback for the like action
 * @param onDislike callback for the dislike action
 * @param onRegenerate callback for the regenerate / retry action
 * @param modifier modifier to apply to the row
 */
@Composable
fun ReactionButtons(
    onLike: () -> Unit,
    onDislike: () -> Unit,
    onRegenerate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ReactionButton("Like", onLike, MaterialTheme.colorScheme.primary)
        ReactionButton("Dislike", onDislike, MaterialTheme.colorScheme.onSurfaceVariant)
        ReactionButton("Regenerate", onRegenerate, MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/**
 * A single reaction pill button.
 */
@Composable
private fun ReactionButton(
    label: String,
    onClick: () -> Unit,
    tint: androidx.compose.ui.graphics.Color,
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        color = tint,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(9999.dp),
        content = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (tint == MaterialTheme.colorScheme.primary) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                ),
            )
        },
    )
}
