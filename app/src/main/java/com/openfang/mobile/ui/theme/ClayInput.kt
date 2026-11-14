package com.openfang.mobile.ui.theme

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A Pastel Clay input: a recessed text field.
 *
 * - Background: surfaceContainerLowest (recessed into the surface)
 * - Shadow: [Modifier.clayPressed] for a compressed / pressed-in appearance
 * - Shape: [Rounded3XL] (32dp)
 * - Cursor: primary
 *
 * @param value the current text value (controlled by the caller)
 * @param onValueChange text change callback
 * @param placeholder placeholder text shown when empty
 * @param modifier modifier to apply to the input
 */
@Composable
fun ClayInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    modifier: Modifier = Modifier,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { if (placeholder.isNotEmpty()) Text(text = placeholder) else null },
        modifier = modifier.fillMaxWidth().clayPressed(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            cursorColor = MaterialTheme.colorScheme.primary,
        ),
        shape = Rounded3XL,
        textStyle = MaterialTheme.typography.bodyMedium,
    )
}
