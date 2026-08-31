package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatMessageEntity
import com.example.data.ConversationEntity
import com.example.ui.AppScreen
import com.example.ui.MindViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: MindViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val settings by viewModel.settings.collectAsState()
    val messages by viewModel.activeMessages.collectAsState()
    val conversations by viewModel.allConversations.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val activeConvId by viewModel.activeConversationId.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var selectedToolType by remember { mutableStateOf<String?>(null) }
    var showChatSettingsDialog by remember { mutableStateOf(false) }
    var showHistoryDrawer by remember { mutableStateOf(false) }
    var showAttachMenu by remember { mutableStateOf(false) }

    val activeConv = conversations.firstOrNull { it.id == activeConvId }

    // Auto-scroll when messages change
    LaunchedEffect(messages.size, isGenerating) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(settings.themeAesthetic.backgroundColorHex))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // --- TOP APP BAR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ← Home Button
                Surface(
                    shape = CircleShape,
                    color = Slate800.copy(alpha = 0.7f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate700.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(AppScreen.HOME) }
                        .testTag("chat_back_home_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Home",
                            tint = Slate100,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Home",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Slate100
                            )
                        )
                    }
                }

                // Assistant Title
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = settings.assistantName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Slate100,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = activeConv?.title ?: "AI Assistant",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyanAccent,
                            fontSize = 10.sp
                        ),
                        maxLines = 1
                    )
                }

                // Right Actions: History & Settings
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = CircleShape,
                        color = Slate800.copy(alpha = 0.7f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate700.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .size(38.dp)
                            .clickable { showHistoryDrawer = true }
                            .testTag("chat_history_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = "History",
                                tint = Slate300,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = Slate800.copy(alpha = 0.7f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate700.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .size(38.dp)
                            .clickable { showChatSettingsDialog = true }
                            .testTag("chat_settings_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "⚙️", fontSize = 16.sp)
                        }
                    }
                }
            }

            // Quick Tool Chips Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val chips = listOf(
                    "🌐 Web Search" to "web_search",
                    "🔬 Deep Research" to "deep_search",
                    "🎨 Image Gen" to "image_gen",
                    "⚡ Summarize" to "summarize",
                    "🗣️ Egyptian Arabic" to "egyptian"
                )

                items(chips) { (label, type) ->
                    val isSelected = selectedToolType == type
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) CyanAccent.copy(alpha = 0.25f) else Slate800.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) CyanAccent else Slate700.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.clickable {
                            selectedToolType = if (isSelected) null else type
                        }
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) CyanAccent else Slate300,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // --- MESSAGES LIST ---
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                if (messages.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = CyanAccent.copy(alpha = 0.15f),
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "✨", fontSize = 28.sp)
                                }
                            }
                            Text(
                                text = "How can ${settings.assistantName} help you today?",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Slate100,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Text(
                                text = "Ask questions, manage design projects, search research, or capture receipts.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Slate400,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                ),
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }
                }

                items(messages) { msg ->
                    ChatMessageBubble(
                        message = msg,
                        assistantName = settings.assistantName,
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("MIND", msg.text)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        onRegenerate = {
                            viewModel.sendMessage("Regenerate response: ${msg.text.take(40)}")
                        }
                    )
                }

                if (isGenerating) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = CyanAccent,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "${settings.assistantName} is thinking...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Slate400,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            TextButton(
                                onClick = { viewModel.stopGeneration() },
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text(
                                    text = "Stop",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MindError,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // --- BOTTOM EXPANDABLE CHAT INPUT ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Attached Tool Indicator
                AnimatedVisibility(visible = selectedToolType != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tool Active: ${selectedToolType?.replace("_", " ")?.uppercase()}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        IconButton(
                            onClick = { selectedToolType = null },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Slate400, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(DarkNavyCard.copy(alpha = 0.95f))
                        .border(1.dp, Slate700.copy(alpha = 0.6f), RoundedCornerShape(28.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // + Attachment / Capture Button
                    IconButton(
                        onClick = { viewModel.openSmartCapture() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Slate800)
                            .testTag("chat_attach_button")
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Capture & Tools",
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Text Field Input
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                text = "Message ${settings.assistantName}...",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Slate500)
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Slate100,
                            unfocusedTextColor = Slate100
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        maxLines = 4
                    )

                    // 🎤 Voice Input Action
                    IconButton(
                        onClick = { viewModel.startVoiceSession() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text(text = "🎤", fontSize = 18.sp)
                    }

                    // Send Button
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendMessage(inputText, toolType = selectedToolType)
                                inputText = ""
                                selectedToolType = null
                            }
                        },
                        enabled = inputText.isNotBlank(),
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (inputText.isNotBlank()) CyanAccent else Slate800)
                            .testTag("chat_send_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (inputText.isNotBlank()) Slate900 else Slate500,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // --- HISTORY DRAWER / MODAL ---
        if (showHistoryDrawer) {
            ChatHistoryModal(
                conversations = conversations,
                activeConvId = activeConvId,
                onSelect = { id ->
                    viewModel.selectConversation(id)
                    showHistoryDrawer = false
                },
                onNewChat = {
                    viewModel.startNewChat()
                    showHistoryDrawer = false
                },
                onDelete = { id -> viewModel.deleteConversation(id) },
                onDismiss = { showHistoryDrawer = false }
            )
        }

        // --- CHAT SETTINGS MODAL ---
        if (showChatSettingsDialog) {
            ChatSettingsModal(
                assistantName = settings.assistantName,
                aiLanguage = settings.aiLanguage,
                responseStyle = settings.responseStyle,
                onDismiss = { showChatSettingsDialog = false }
            )
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessageEntity,
    assistantName: String,
    onCopy: () -> Unit,
    onRegenerate: () -> Unit
) {
    val isUser = message.sender == "user"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        if (!isUser) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
            ) {
                Text(text = "✨", fontSize = 12.sp)
                Text(
                    text = assistantName,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Box(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (isUser) 20.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 20.dp
                    )
                )
                .background(
                    if (isUser) Brush.linearGradient(listOf(Slate800, DarkNavyCard))
                    else Brush.linearGradient(listOf(ElevatedNavySurface, DarkNavyCard))
                )
                .border(
                    1.dp,
                    if (isUser) Slate700.copy(alpha = 0.5f) else CyanAccent.copy(alpha = 0.2f),
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (isUser) 20.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 20.dp
                    )
                )
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Slate100,
                        lineHeight = 21.sp
                    )
                )

                if (!message.sources.isNullOrBlank()) {
                    HorizontalDivider(color = Slate700.copy(alpha = 0.4f))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "🔗", fontSize = 10.sp)
                        Text(
                            text = "Sources: ${message.sources}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Slate400,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }

        if (!isUser) {
            Row(
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onCopy, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy", tint = Slate400, modifier = Modifier.size(13.dp))
                }
                IconButton(onClick = onRegenerate, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Outlined.Refresh, contentDescription = "Regenerate", tint = Slate400, modifier = Modifier.size(13.dp))
                }
            }
        }
    }
}

@Composable
fun ChatHistoryModal(
    conversations: List<ConversationEntity>,
    activeConvId: String,
    onSelect: (String) -> Unit,
    onNewChat: () -> Unit,
    onDelete: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chat History",
                    style = MaterialTheme.typography.titleMedium.copy(color = Slate100, fontWeight = FontWeight.Bold)
                )
                Button(
                    onClick = onNewChat,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Slate900),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = "+ New Chat", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 360.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(conversations) { conv ->
                    val isSelected = conv.id == activeConvId
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) CyanAccent.copy(alpha = 0.15f) else Slate800.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CyanAccent else Slate700),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(conv.id) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = conv.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isSelected) CyanAccent else Slate100,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    maxLines = 1
                                )
                                Text(
                                    text = "Active session",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 10.sp)
                                )
                            }
                            IconButton(onClick = { onDelete(conv.id) }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Slate400, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        },
        containerColor = DarkNavyCard
    )
}

@Composable
fun ChatSettingsModal(
    assistantName: String,
    aiLanguage: String,
    responseStyle: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = CyanAccent, fontWeight = FontWeight.Bold)
            }
        },
        title = {
            Text(
                text = "Chat Settings",
                style = MaterialTheme.typography.titleMedium.copy(color = Slate100, fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Active Model: Gemini 2.5 Flash",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate300)
                )
                Text(
                    text = "AI Language: $aiLanguage",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate300)
                )
                Text(
                    text = "Response Style: $responseStyle",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate300)
                )
                Text(
                    text = "Memory Retention: Active",
                    style = MaterialTheme.typography.bodySmall.copy(color = MindSuccess)
                )
            }
        },
        containerColor = DarkNavyCard
    )
}
