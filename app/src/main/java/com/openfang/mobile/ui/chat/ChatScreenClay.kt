package com.openfang.mobile.ui.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openfang.mobile.ui.ChatMessage
import com.openfang.mobile.ui.ChatVm
import com.openfang.mobile.ui.theme.ClayButton
import com.openfang.mobile.ui.theme.ClayCard
import com.openfang.mobile.ui.theme.ClayInput
import com.openfang.mobile.ui.theme.FloatingNavDock
import com.openfang.mobile.ui.theme.ModelSelector
import com.openfang.mobile.ui.theme.PillShape
import com.openfang.mobile.ui.theme.Rounded3XL
import com.openfang.mobile.ui.theme.SidebarDrawer
import com.openfang.mobile.ui.theme.components.MessageBubbleClay
import com.openfang.mobile.ui.theme.components.PromptPill

// ---------------------------------------------------------------------------
// Pure helpers (testable without Compose)
// ---------------------------------------------------------------------------

/**
 * Whether the streaming indicator should be shown below the message list.
 *
 * Shows when the ViewModel is busy or when any message is still streaming.
 */
fun streamingIndicatorVisible(isBusy: Boolean, messages: List<ChatMessage>): Boolean =
    isBusy || messages.any { it.isStreaming }

/** Quota widget display text: "used / total MB". */
fun quotaText(used: Int, total: Int): String = "$used / $total MB"

// ---------------------------------------------------------------------------
// Screen
// ---------------------------------------------------------------------------

/**
 * The main chat screen in the Pastel Clay design system.
 *
 * Replaces [ChatScreen]. Renders:
 *  - Sidebar drawer (left): profile, "Chat Baru", chat history, quota widget.
 *  - Sticky [ModelSelector] above the message list.
 *  - Message list: [LazyColumn] with [MessageBubbleClay], streaming indicator,
 *    and an auth error banner.
 *  - Horizontal [PromptPill] suggestions.
 *  - Chat input dock: [ClayInput] textarea, attachment, microphone, send.
 *  - Bottom: [FloatingNavDock].
 *
 * @param vm the [ChatVm] (obtained via [hiltViewModel] by default).
 */
@Composable
fun ChatScreenClay(vm: ChatVm = hiltViewModel()) {
    val st by vm.state.collectAsState()
    val drawerOpen = remember { mutableStateOf(false) }
    val input = remember { mutableStateOf("") }
    val selectedModel = remember { mutableStateOf("Claude 3.5 Sonnet") }
    val selectedTab = remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        // -------------------------------------------------------------------
        // Main content
        // -------------------------------------------------------------------
        Column(modifier = Modifier.fillMaxSize()) {
            // Sticky model selector above the message list.
            ModelSelector(
                selectedModel = selectedModel.value,
                onModelClick = { selectedModel.value = it },
                modifier = Modifier.padding(horizontal = 16.dp).padding(top = 8.dp),
            )

            // Auth error banner (Review Focus #5).
            if (st.authError) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "⚠ Authentication failed. Re-enter your API token in Settings.",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f),
                        )
                        TextButton(onClick = { vm.clearAuthError() }) {
                            Text(text = "OK")
                        }
                    }
                }
            }

            // Message list.
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(st.messages) { msg ->
                    MessageBubbleClay(
                        msg = msg,
                        onLike = {},
                        onDislike = {},
                        onRegenerate = {},
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                if (streamingIndicatorVisible(st.isBusy, st.messages)) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "streaming…",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            // Prompt pills (horizontal).
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PromptPill(label = "Buat ringkasan", onClick = {})
                PromptPill(label = "Tolong coding", onClick = {})
                PromptPill(label = "Analisis data", onClick = {})
            }

            // Chat input dock (textarea, attachment, microphone, kirim).
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                ClayInput(
                    value = input.value,
                    onValueChange = { input.value = it },
                    placeholder = "Ketik pesan…",
                    modifier = Modifier.weight(1f),
                )
                actionChip("📎")
                actionChip("🎤")
                ClayButton(
                    onClick = {
                        if (input.value.isNotBlank()) {
                            vm.send(input.value)
                            input.value = ""
                        }
                    },
                    content = { Text(text = "Kirim") },
                    enabled = !st.isBusy && input.value.isNotBlank(),
                )
            }

            // Bottom: Floating nav dock.
            FloatingNavDock(
                selectedTab = selectedTab.value,
                onTabClick = { selectedTab.value = it },
            )
        }

        // -------------------------------------------------------------------
        // Sidebar drawer (left overlay)
        // -------------------------------------------------------------------
        SidebarDrawer(
            isOpen = drawerOpen.value,
            onClose = { drawerOpen.value = false },
            content = {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Profile.
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(40.dp).clip(Rounded3XL),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            content = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "👤",
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            },
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Anda",
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = "openfang.local",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    // Chat Baru.
                    ClayButton(
                        onClick = { drawerOpen.value = false },
                        content = { Text(text = "Chat Baru") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Riwayat chat grouped.
                    Text(
                        text = "Riwayat",
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    st.messages.filter { it.role == "user" }.forEach { msg ->
                        Text(
                            text = msg.text.take(30),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    // Quota widget.
                    ClayCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = quotaText(used = 120, total = 500),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            },
        )
    }
}

/** Small round action chip for attachment / microphone. */
@Composable
private fun actionChip(label: String) {
    Surface(
        modifier = Modifier.size(40.dp).clip(PillShape),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        content = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        },
    )
}
