package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MindViewModel
import com.example.ui.theme.*

data class CaptureOption(
    val id: String,
    val title: String,
    val icon: String,
    val sampleContent: String
)

@Composable
fun SmartCaptureDialog(
    viewModel: MindViewModel,
    onDismiss: () -> Unit
) {
    val isGenerating by viewModel.isGenerating.collectAsState()
    val captureResult by viewModel.smartCaptureResult.collectAsState()

    var manualTextInput by remember { mutableStateOf("") }
    var selectedOption by remember { mutableStateOf<CaptureOption?>(null) }

    val captureOptions = listOf(
        CaptureOption("screenshot", "Flight Screenshot", "📱", "Emirates Flight EK924 Sep 14, 8:30 AM to DXB"),
        CaptureOption("receipt", "Expense Receipt", "🧾", "Samir & Aly Art Supply Store: 450 EGP (Foam board & markers)"),
        CaptureOption("voice", "Voice Memo", "🎙️", "Audio recording: Remember to buy foam board and finish 3D rendering"),
        CaptureOption("document", "PDF Document", "📄", "Project_Brief_Interior_Winter_2026.pdf"),
        CaptureOption("image", "Camera Photo", "📷", "Studio material moodboard snapshot"),
        CaptureOption("link", "Web Link", "🔗", "https://archdaily.com/nordic-winter-materials-2026")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            if (captureResult != null) {
                Button(
                    onClick = { viewModel.confirmCaptureAction() },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Slate900),
                    modifier = Modifier.testTag("confirm_capture_action_button")
                ) {
                    Text(
                        text = if (captureResult?.actionType == "ADD_REMINDER") "Add to Reminders"
                        else if (captureResult?.actionType == "ADD_TASK") "Add to Tasks"
                        else "Save to Memory",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Close", color = Slate400)
            }
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "⚡", fontSize = 20.sp)
                Text(
                    text = "Smart Capture",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Slate100,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (captureResult == null) {
                    Text(
                        text = "Select an input or sample to instantly extract tasks, reminders, and schedule data:",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate300)
                    )

                    // Options Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(190.dp)
                    ) {
                        items(captureOptions) { option ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Slate800.copy(alpha = 0.8f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedOption = option
                                        viewModel.triggerSmartCapture(option.id, option.sampleContent)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = option.icon, fontSize = 20.sp)
                                    Text(
                                        text = option.title,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Slate100,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    // Custom quick text input
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Slate800)
                            .border(1.dp, Slate700, RoundedCornerShape(16.dp))
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = manualTextInput,
                            onValueChange = { manualTextInput = it },
                            placeholder = {
                                Text("Or type note/link...", style = MaterialTheme.typography.bodySmall.copy(color = Slate500))
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Slate100,
                                unfocusedTextColor = Slate100
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                if (manualTextInput.isNotBlank()) {
                                    viewModel.triggerSmartCapture("text", manualTextInput)
                                    manualTextInput = ""
                                }
                            },
                            enabled = manualTextInput.isNotBlank()
                        ) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Process", tint = CyanAccent)
                        }
                    }
                }

                if (isGenerating) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = CyanAccent, strokeWidth = 2.dp)
                        Text(
                            text = "Analyzing content & extracting details...",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate300)
                        )
                    }
                }

                captureResult?.let { result ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = ElevatedNavySurface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "✨", fontSize = 14.sp)
                                Text(
                                    text = "EXTRACTED SUMMARY",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CyanAccent,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Text(
                                text = result.summaryText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Slate100,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }
            }
        },
        containerColor = DarkNavyCard,
        shape = RoundedCornerShape(28.dp)
    )
}
