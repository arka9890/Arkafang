package com.openfang.mobile.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A selectable model option.
 *
 * @param name display name of the model
 * @param provider provider that hosts the model
 */
data class ModelOption(
    val name: String,
    val provider: String,
)

/** The four models available in the selector. */
val AvailableModels = listOf(
    ModelOption("Claude 3.5 Sonnet", "Anthropic"),
    ModelOption("GPT-4o", "OpenAI"),
    ModelOption("Gemini 1.5 Pro", "Google"),
    ModelOption("Llama 3.3 70B", "Meta"),
)

/**
 * A sticky top bar showing the active model in a clay card, with a dropdown
 * panel listing the available models.
 *
 * - Top bar: [ClayCard] with the active model icon + name + provider
 * - Dropdown panel: one [ClayChip] per available model
 *
 * @param selectedModel the currently selected model name
 * @param onModelClick callback invoked with the selected model name
 * @param modifier modifier applied to the selector
 */
@Composable
fun ModelSelector(
    selectedModel: String,
    onModelClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val selected = AvailableModels.firstOrNull { it.name == selectedModel }
        ?: AvailableModels.first()

    Column(modifier = modifier.fillMaxWidth()) {
        // Sticky top bar — active model clay card.
        ClayCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded },
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "\uD83E\uDD16",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp),
                )
                Text(
                    text = selected.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = " \u00b7 ${selected.provider}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Dropdown panel — one chip per model.
        if (isExpanded) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = Rounded3XL,
                content = {
                    Column {
                        AvailableModels.forEach { model ->
                            ClayChip(
                                label = "${model.name} (${model.provider})",
                                selected = model.name == selectedModel,
                                onClick = {
                                    onModelClick(model.name)
                                },
                            )
                        }
                    }
                },
            )
        }
    }
}
