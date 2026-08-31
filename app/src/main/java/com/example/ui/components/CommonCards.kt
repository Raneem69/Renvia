package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HealthMetricEntity
import com.example.data.MemoryEntity
import com.example.data.TaskEntity
import com.example.ui.theme.*

@Composable
fun WeatherCard(
    isCelsius: Boolean,
    onToggleUnit: () -> Unit,
    location: String = "Cairo",
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val tempDisplay = if (isCelsius) "24°C" else "75°F"

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Slate800.copy(alpha = 0.7f),
                        DarkNavyCard.copy(alpha = 0.9f)
                    )
                )
            )
            .border(1.dp, Slate700.copy(alpha = 0.5f), RoundedCornerShape(28.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag("weather_card")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "☀️",
                    fontSize = 24.sp
                )
                Surface(
                    shape = CircleShape,
                    color = Slate700.copy(alpha = 0.5f),
                    modifier = Modifier.clickable(onClick = onToggleUnit)
                ) {
                    Text(
                        text = location.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate300,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Column {
                Text(
                    text = tempDisplay,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate100,
                        fontSize = 28.sp
                    )
                )
                Text(
                    text = "MOSTLY CLEAR",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }
        }
    }
}

@Composable
fun TasksOverviewCard(
    tasks: List<TaskEntity>,
    onToggleTask: (Long, Boolean) -> Unit,
    onOpenTasks: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completedCount = tasks.count { it.isCompleted }
    val totalCount = if (tasks.isNotEmpty()) tasks.size else 5
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f
    val pendingTask = tasks.firstOrNull { !it.isCompleted }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Slate800.copy(alpha = 0.7f),
                        DarkNavyCard.copy(alpha = 0.9f)
                    )
                )
            )
            .border(1.dp, Slate700.copy(alpha = 0.5f), RoundedCornerShape(28.dp))
            .clickable(onClick = onOpenTasks)
            .padding(16.dp)
            .testTag("tasks_card")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TASKS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Slate400,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Surface(
                    shape = CircleShape,
                    color = CyanAccent.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "$completedCount/$totalCount",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyanAccent,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // Progress track
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(Slate700)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.coerceIn(0.1f, 1f))
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(CyanAccent)
                    )
                }

                Text(
                    text = pendingTask?.title ?: "All tasks completed! 🎉",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Slate300,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun DailyBriefCard(
    assistantName: String = "MIND",
    onAskMind: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        ElevatedNavySurface.copy(alpha = 0.9f),
                        DarkNavyCard.copy(alpha = 0.95f)
                    )
                )
            )
            .border(1.dp, CyanAccent.copy(alpha = 0.25f), RoundedCornerShape(32.dp))
            .padding(20.dp)
            .testTag("daily_brief_card")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "✨",
                        fontSize = 16.sp
                    )
                    Text(
                        text = "YOUR DAY",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = CyanAccent,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = CyanAccent.copy(alpha = 0.12f),
                    modifier = Modifier.clickable(onClick = onAskMind)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Ask $assistantName",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "Ask",
                            tint = CyanAccent,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Text(
                text = "You have a balanced day today. Focus on your interior design project deliverables due Friday. Sync meeting scheduled for 4:00 PM.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Slate300,
                    lineHeight = 20.sp
                )
            )
        }
    }
}

@Composable
fun QuickActionsRow(
    onCapture: () -> Unit,
    onVoice: () -> Unit,
    onTools: () -> Unit,
    onMemory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // + Capture Main Button
        Button(
            onClick = onCapture,
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CyanAccent,
                contentColor = Slate900
            ),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .testTag("quick_capture_button"),
            contentPadding = PaddingValues(horizontal = 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Capture",
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "CAPTURE",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }
        }

        // Voice Action
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Slate800.copy(alpha = 0.8f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
            modifier = Modifier
                .size(52.dp)
                .clickable(onClick = onVoice)
                .testTag("quick_voice_button")
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "🎤", fontSize = 18.sp)
            }
        }

        // Tools Action
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Slate800.copy(alpha = 0.8f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
            modifier = Modifier
                .size(52.dp)
                .clickable(onClick = onTools)
                .testTag("quick_tools_button")
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "✨", fontSize = 18.sp)
            }
        }

        // Memory Action
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Slate800.copy(alpha = 0.8f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
            modifier = Modifier
                .size(52.dp)
                .clickable(onClick = onMemory)
                .testTag("quick_memory_button")
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = "🧠", fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun MemoryPreviewCard(
    memory: MemoryEntity?,
    onViewMemory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(DarkNavyCard.copy(alpha = 0.7f))
            .border(1.dp, Slate800, RoundedCornerShape(24.dp))
            .clickable(onClick = onViewMemory)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("memory_preview_card")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CyanAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🧠", fontSize = 16.sp)
                }

                Text(
                    text = memory?.content ?: "Project uses a winter color palette with icy blues",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Slate300,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            TextButton(
                onClick = onViewMemory,
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text(
                    text = "VIEW",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun StepsHealthWidget(
    metric: HealthMetricEntity?,
    onAddSteps: () -> Unit,
    onOpenHealth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val steps = metric?.steps ?: 6482
    val goal = metric?.stepGoal ?: 10000
    val progress = (steps.toFloat() / goal.toFloat()).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Slate800.copy(alpha = 0.5f),
                        DarkNavyCard.copy(alpha = 0.85f)
                    )
                )
            )
            .border(1.dp, Slate700.copy(alpha = 0.4f), RoundedCornerShape(28.dp))
            .clickable(onClick = onOpenHealth)
            .padding(16.dp)
            .testTag("health_widget")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MindSuccess.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👟", fontSize = 20.sp)
                }

                Column {
                    Text(
                        text = "STEPS TODAY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate400,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "$steps / $goal",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Slate100,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Surface(
                shape = CircleShape,
                color = MindSuccess.copy(alpha = 0.15f),
                modifier = Modifier.clickable(onClick = onAddSteps)
            ) {
                Text(
                    text = "+250",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MindSuccess,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}
