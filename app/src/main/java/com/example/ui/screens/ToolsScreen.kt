package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.MindViewModel
import com.example.ui.theme.*

data class ToolItem(
    val id: String,
    val icon: String,
    val title: String,
    val subtitle: String,
    val isPro: Boolean = false,
    val samplePrompt: String
)

@Composable
fun ToolsScreen(
    viewModel: MindViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()

    val toolsList = listOf(
        ToolItem("image_gen", "🎨", "Image Studio", "Text-to-image concept renders & variations", false, "Generate an image of a luxury minimalist interior with winter palette"),
        ToolItem("deep_search", "🔬", "Deep Research", "Multi-source citation reports & comparison", true, "Deep research on interior architecture trends and sustainable composites for 2026"),
        ToolItem("web_search", "🌐", "Web Search", "Live facts with cited sources", false, "Search current weather radar and events in Cairo"),
        ToolItem("video_gen", "🎬", "Video Generation", "Text-to-video 3D animation simulation", true, "Generate a 5-second cinematic slow camera pan through a modern minimalist room"),
        ToolItem("translate", "🗣️", "Smart Translator", "Natural translation with Egyptian Arabic support", false, "Translate 'The winter moodboard looks great' into authentic Egyptian Arabic"),
        ToolItem("doc_analysis", "📄", "Document & PDF Analysis", "Summarize briefs, syllabi, contracts & receipts", false, "Analyze the project requirements and extract key deadlines"),
        ToolItem("coding", "💻", "Code & Math Helper", "Algorithm writing, debugging & calculations", false, "Calculate scale proportions for architectural foam board prototype")
    )

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
                .padding(horizontal = 20.dp)
        ) {
            // --- TOP BAR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(AppScreen.HOME) },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Slate800.copy(alpha = 0.7f))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Slate100)
                }

                Text(
                    text = "AI TOOLS",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate100,
                        letterSpacing = 2.sp
                    )
                )

                Surface(
                    shape = CircleShape,
                    color = CyanAccent.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (settings.isPro) "PRO ACTIVE" else "FREE TIER",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyanAccent,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            // --- TOOLS LIST ---
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(toolsList) { tool ->
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = DarkNavyCard.copy(alpha = 0.85f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate700.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.sendMessage(tool.samplePrompt, toolType = tool.id)
                                viewModel.navigateTo(AppScreen.CHAT)
                            }
                            .testTag("tool_card_${tool.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(ElevatedNavySurface),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = tool.icon, fontSize = 24.sp)
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = tool.title,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            color = Slate100,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    if (tool.isPro) {
                                        Surface(
                                            shape = CircleShape,
                                            color = MindPurple.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "PRO",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = MindPurple,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 9.sp
                                                ),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = tool.subtitle,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                                )
                            }

                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = "Launch",
                                tint = CyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
