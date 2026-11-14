package com.openfang.mobile.ui.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.openfang.mobile.ui.HandState
import com.openfang.mobile.ui.theme.ClayButton
import com.openfang.mobile.ui.theme.ClayCard
import com.openfang.mobile.ui.theme.ClayChip
import com.openfang.mobile.ui.theme.PillShape
import com.openfang.mobile.ui.theme.clayElevation
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

// ---------------------------------------------------------------------------
// Pure, testable helpers
// ---------------------------------------------------------------------------

/**
 * Parses a HandState.metrics JSON string into a list of `key: value` pairs
 * for display. Returns an empty list on parse failure (defensive — a
 * malformed metric should not crash the UI).
 */
internal fun parseMetricsJson(metricsJson: String): List<Pair<String, String>> {
    return try {
        val obj = Json.parseToJsonElement(metricsJson) as? JsonObject ?: return emptyList()
        obj.entries.map { (k, v) ->
            val valueStr = if (v is JsonPrimitive) v.content.toString() else v.toString()
            k to valueStr
        }
    } catch (_: Exception) {
        emptyList()
    }
}

/** Returns the status chip color: active color when running/active, otherwise inactive color. */
internal fun statusColor(
    status: String,
    activeColor: Color,
    inactiveColor: Color,
): Color {
    return when (status.lowercase()) {
        "running", "active", "enabled" -> activeColor
        else -> inactiveColor
    }
}

/**
 * Formats a last-run epoch-millisecond timestamp into a short display string.
 * Returns "never" when the timestamp is null.
 */
internal fun formatLastRun(lastRunAt: Long?): String {
    return lastRunAt?.let { "$it ms ago" } ?: "never"
}

// ---------------------------------------------------------------------------
// Component
// ---------------------------------------------------------------------------

/**
 * A Pastel Clay dashboard hand card.
 *
 * Displays the hand name, a StatusPill (clay chip) reflecting the hand's
 * status, parsed metrics, Activate/Pause pill buttons, and the last-run
 * timestamp.
 *
 * @param hand the [HandState] to render
 * @param onActivate callback invoked when the Activate button is pressed
 * @param onPause callback invoked when the Pause button is pressed
 * @param modifier modifier to apply to the card
 */
@Composable
fun HandCardClay(
    hand: HandState,
    onActivate: () -> Unit,
    onPause: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isActive = hand.status.lowercase() == "running" || hand.status.lowercase() == "active"
    val metrics = parseMetricsJson(hand.metrics)

    ClayCard(
        modifier = modifier.clayElevation(),
        content = {
            Column {
                // Hand name + status pill row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                ) {
                    Text(
                        text = hand.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    ClayChip(
                        label = hand.status.capitalize(),
                        selected = isActive,
                        onClick = null,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Metrics (key: value pairs)
                if (metrics.isNotEmpty()) {
                    Column {
                        metrics.forEachIndexed { index, (key, value) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = key,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = value,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                            if (index < metrics.size - 1) {
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Activate / Pause pill buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    ClayButton(
                        onClick = onActivate,
                        content = {
                            Text(
                                text = "Activate",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        },
                        enabled = !isActive,
                    )
                    ClayButton(
                        onClick = onPause,
                        content = {
                            Text(
                                text = "Pause",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        },
                        enabled = isActive,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Last run timestamp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(
                        text = "Last run: ${formatLastRun(hand.lastRunAt)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
    )
}
