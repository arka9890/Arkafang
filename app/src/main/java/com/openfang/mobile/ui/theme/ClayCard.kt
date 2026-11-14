package com.openfang.mobile.ui.theme

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A Pastel Clay card: a raised surface container.
 *
 * - Fill: [MaterialTheme.colorScheme.surfaceContainerLow]
 * - Shape: [Rounded3XL] (32dp)
 * - Padding: 20dp (space-lg)
 * - Shadow: [Modifier.clayElevation]
 *
 * @param modifier modifier to apply to the card
 * @param content the card content
 */
@Composable
fun ClayCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.clayElevation().padding(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = Rounded3XL,
        content = content,
    )
}
