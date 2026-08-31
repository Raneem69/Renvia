package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.MindViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import java.util.Calendar

@Composable
fun HomeScreen(
    viewModel: MindViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    val tasks by viewModel.allTasks.collectAsState()
    val memories by viewModel.allMemories.collectAsState()
    val health by viewModel.healthMetric.collectAsState()

    // Determine time-based greeting
    val currentHour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val greetingPrefix = when (currentHour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..21 -> "Good evening"
        else -> "Good night"
    }
    val greetingEmoji = when (currentHour) {
        in 5..11 -> "👋"
        in 12..16 -> "☀️"
        in 17..21 -> "🌙"
        else -> "✨"
    }

    val recentMemory = memories.firstOrNull()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(settings.themeAesthetic.backgroundColorHex))
    ) {
        // Ambient decorative glow blurs
        Box(
            modifier = Modifier
                .size(240.dp)
                .offset(x = (-40).dp, y = (-40).dp)
                .clip(CircleShape)
                .background(Color(settings.themeAesthetic.primaryColorHex).copy(alpha = 0.12f))
                .blur(80.dp)
        )
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = 60.dp)
                .clip(CircleShape)
                .background(Color(settings.themeAesthetic.accentColorHex).copy(alpha = 0.12f))
                .blur(90.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // --- HEADER ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Top-Left: Chat Button
                Surface(
                    shape = CircleShape,
                    color = Slate800.copy(alpha = 0.7f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate700.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(AppScreen.CHAT) }
                        .testTag("home_chat_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "💬", fontSize = 16.sp)
                        Text(
                            text = "Chat",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Slate100
                            )
                        )
                    }
                }

                // Top-Center: MIND Title
                Text(
                    text = settings.assistantName.uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp,
                        color = Slate400
                    )
                )

                // Top-Right: Settings Button
                Surface(
                    shape = CircleShape,
                    color = Slate800.copy(alpha = 0.7f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate700.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { viewModel.navigateTo(AppScreen.SETTINGS) }
                        .testTag("home_settings_button")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "⚙️", fontSize = 16.sp)
                    }
                }
            }

            // --- SCROLLABLE CONTENT ---
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
            ) {
                // --- PERSONALIZED GREETING ---
                item {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$greetingPrefix, ",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Light,
                                    color = Slate100,
                                    fontSize = 26.sp
                                )
                            )
                            Text(
                                text = settings.userName,
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(settings.themeAesthetic.primaryColorHex),
                                    fontSize = 26.sp
                                )
                            )
                            Text(
                                text = " $greetingEmoji",
                                fontSize = 24.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "You have ${tasks.count { !it.isCompleted }} important tasks today, and rain is expected this evening.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Slate400,
                                lineHeight = 20.sp
                            )
                        )
                    }
                }

                // --- 2-COLUMN GRID: WEATHER & TASKS ---
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        WeatherCard(
                            isCelsius = settings.tempUnitCelsius,
                            onToggleUnit = { viewModel.toggleTempUnit() },
                            location = settings.homeLocation.split(",").firstOrNull() ?: "Cairo",
                            modifier = Modifier.weight(1f)
                        )

                        TasksOverviewCard(
                            tasks = tasks,
                            onToggleTask = { id, status -> viewModel.toggleTask(id, status) },
                            onOpenTasks = { viewModel.navigateTo(AppScreen.TODAY_TASKS) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // --- DAILY BRIEF ---
                item {
                    DailyBriefCard(
                        assistantName = settings.assistantName,
                        onAskMind = {
                            viewModel.sendMessage("Tell me more about today's schedule and priorities")
                            viewModel.navigateTo(AppScreen.CHAT)
                        }
                    )
                }

                // --- QUICK ACTIONS ---
                item {
                    QuickActionsRow(
                        onCapture = { viewModel.openSmartCapture() },
                        onVoice = { viewModel.startVoiceSession() },
                        onTools = { viewModel.navigateTo(AppScreen.TOOLS) },
                        onMemory = { viewModel.navigateTo(AppScreen.MEMORY) }
                    )
                }

                // --- MEMORY PREVIEW ---
                item {
                    MemoryPreviewCard(
                        memory = recentMemory,
                        onViewMemory = { viewModel.navigateTo(AppScreen.MEMORY) }
                    )
                }

                // --- AI SUGGESTION CARD ---
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        DarkNavyCard.copy(alpha = 0.8f),
                                        ElevatedNavySurface.copy(alpha = 0.6f)
                                    )
                                )
                            )
                            .border(1.dp, Slate700.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(text = "💡", fontSize = 20.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "SUGGESTION",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CyanAccent,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Text(
                                    text = "Rain is expected around 5 PM when you leave the studio. Consider heading out slightly early.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate300)
                                )
                            }
                        }
                    }
                }

                // --- HEALTH & FITNESS WIDGET ---
                item {
                    StepsHealthWidget(
                        metric = health,
                        onAddSteps = { viewModel.addSteps(250) },
                        onOpenHealth = { viewModel.navigateTo(AppScreen.HEALTH) }
                    )
                }

                // --- TOOLS PREVIEW (Horizontal Row) ---
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "AI TOOLS",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Slate400,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                )
                            )
                            Text(
                                text = "See all",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = CyanAccent,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.TOOLS) }
                            )
                        }

                        val toolsList = listOf(
                            Triple("🎨", "Image Gen", "image_gen"),
                            Triple("🔬", "Deep Research", "deep_search"),
                            Triple("🌐", "Web Search", "web_search"),
                            Triple("🗣️", "Translate", "translate"),
                            Triple("📄", "Analyze File", "file_analysis")
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(toolsList) { tool ->
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Slate800.copy(alpha = 0.6f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate700.copy(alpha = 0.5f)),
                                    modifier = Modifier
                                        .clickable {
                                            viewModel.navigateTo(AppScreen.TOOLS)
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(text = tool.first, fontSize = 16.sp)
                                        Text(
                                            text = tool.second,
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                color = Slate200,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- BOTTOM GESTURE NAVIGATION HINT ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(64.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(Slate700.copy(alpha = 0.5f))
                )
            }
        }
    }
}
