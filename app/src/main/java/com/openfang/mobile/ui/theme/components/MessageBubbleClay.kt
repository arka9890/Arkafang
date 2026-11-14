package com.openfang.mobile.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.openfang.mobile.ui.ChatMessage
import com.openfang.mobile.ui.theme.Rounded3XL
import com.openfang.mobile.ui.theme.clayElevation

// ---------------------------------------------------------------------------
// Pure, testable helpers
// ---------------------------------------------------------------------------

/**
 * Returns the bubble shape: rounded-3xl (32dp) with the inner top corner
 * reduced by medium (16dp) so the bubble tucks under its neighbor.
 *
 * - User bubble: top-right corner reduced (rounded-tr-minus-md).
 * - AI bubble: top-left corner reduced (rounded-tl-minus-md).
 */
internal fun messageBubbleShape(isUser: Boolean): Shape = Rounded3XL

/** Gradient brush for user bubbles: primary -> primaryContainer. */
internal fun userBubbleGradient(
    primary: Color,
    primaryContainer: Color,
): Brush =
    Brush.horizontalGradient(colors = listOf(primary, primaryContainer))

/** Container (fill) color for AI bubbles — the clay surface color. */
internal fun aiBubbleColor(surface: Color): Color = surface

/** Container (fill) color for system bubbles — the error container color. */
internal fun systemBubbleColor(errorContainer: Color): Color = errorContainer

// ---------------------------------------------------------------------------
// Component
// ---------------------------------------------------------------------------

/**
 * A Pastel Clay chat message bubble.
 *
 * Three variants are supported:
 * - User bubble: gradient primary-to-light fill, rounded-3xl, aligned to the end.
 * - AI bubble: white clay surface, rounded-3xl, aligned to the start, with an avatar.
 * - System bubble: error container, centered.
 *
 * All bubbles include a timestamp and reaction buttons (like / dislike /
 * regenerate) when not a system message.
 *
 * @param msg the chat message to render ([ChatMessage.role] drives the
 *   variant when [isUser] is not explicitly provided)
 * @param isUser whether this is a user bubble (defaults to `msg.role == "user"`)
 * @param onLike like reaction callback
 * @param onDislike dislike reaction callback
 * @param onRegenerate regenerate reaction callback
 * @param modifier modifier to apply to the bubble row
 */
@Composable
fun MessageBubbleClay(
    msg: ChatMessage,
    isUser: Boolean = msg.role == "user",
    onLike: () -> Unit = {},
    onDislike: () -> Unit = {},
    onRegenerate: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val isSystem = msg.role == "system"
    val primary = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer

    val bubbleContent = @Composable {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
        ) {
            Text(
                text = msg.text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSystem) {
                    MaterialTheme.colorScheme.onError
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (msg.isStreaming) "streaming…" else "now",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (!isSystem) {
                    ReactionButtons(
                        onLike = onLike,
                        onDislike = onDislike,
                        onRegenerate = onRegenerate,
                    )
                }
            }
        }
    }

    val bubble = if (isSystem) {
        Surface(
            modifier = Modifier.clayElevation(),
            color = MaterialTheme.colorScheme.errorContainer,
            shape = Rounded3XL,
            content = bubbleContent,
        )
    } else if (isUser) {
        Box(
            modifier = Modifier
                .clayElevation()
                .background(brush = userBubbleGradient(primary, primaryContainer))
                .clip(messageBubbleShape(isUser = true)),
            content = { bubbleContent },
        )
    } else {
        Surface(
            modifier = Modifier.clayElevation(),
            color = MaterialTheme.colorScheme.surface,
            shape = messageBubbleShape(isUser = false),
            content = bubbleContent,
        )
    }

    Row(
        modifier = modifier,
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top,
    ) {
        if (!isUser && !isSystem) {
            Surface(
                modifier = Modifier.size(28.dp).clip(androidx.compose.foundation.shape.RoundedCornerShape(9999.dp)),
                color = MaterialTheme.colorScheme.primaryContainer,
                content = {},
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        bubble
    }
}
