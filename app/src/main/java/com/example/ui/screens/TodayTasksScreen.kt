package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ReminderEntity
import com.example.data.TaskEntity
import com.example.ui.AppScreen
import com.example.ui.MindViewModel
import com.example.ui.theme.*

@Composable
fun TodayTasksScreen(
    viewModel: MindViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    val tasks by viewModel.allTasks.collectAsState()
    val reminders by viewModel.allReminders.collectAsState()

    var activeTab by remember { mutableStateOf(0) } // 0 = Tasks, 1 = Reminders
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showAddReminderDialog by remember { mutableStateOf(false) }

    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskPriority by remember { mutableStateOf("Medium") }
    var newTaskDeadline by remember { mutableStateOf("Today") }

    var newReminderTitle by remember { mutableStateOf("") }
    var newReminderTime by remember { mutableStateOf("5:00 PM") }
    var newReminderLocation by remember { mutableStateOf("") }

    val completedCount = tasks.count { it.isCompleted }
    val totalCount = tasks.size

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
                    text = "ORGANIZER",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate100,
                        letterSpacing = 2.sp
                    )
                )

                IconButton(
                    onClick = {
                        if (activeTab == 0) showAddTaskDialog = true else showAddReminderDialog = true
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CyanAccent)
                        .testTag("add_item_organizer_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = Slate900)
                }
            }

            // --- TAB SELECTOR (Tasks vs Reminders) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Slate800.copy(alpha = 0.6f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (activeTab == 0) CyanAccent else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeTab = 0 }
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Today's Tasks ($completedCount/$totalCount)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (activeTab == 0) Slate900 else Slate300,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (activeTab == 1) CyanAccent else Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeTab = 1 }
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Reminders (${reminders.size})",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (activeTab == 1) Slate900 else Slate300,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // --- CONTENT LIST ---
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                if (activeTab == 0) {
                    // Tasks Tab
                    items(tasks) { task ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = DarkNavyCard.copy(alpha = if (task.isCompleted) 0.5f else 0.9f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.toggleTask(task.id, task.isCompleted) }
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Checkbox(
                                        checked = task.isCompleted,
                                        onCheckedChange = { viewModel.toggleTask(task.id, task.isCompleted) },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = CyanAccent,
                                            checkmarkColor = Slate900,
                                            uncheckedColor = Slate500
                                        )
                                    )

                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = task.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = if (task.isCompleted) Slate400 else Slate100,
                                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = CircleShape,
                                                color = when (task.priority) {
                                                    "High" -> MindError.copy(alpha = 0.2f)
                                                    "Medium" -> MindWarning.copy(alpha = 0.2f)
                                                    else -> MindSuccess.copy(alpha = 0.2f)
                                                }
                                            ) {
                                                Text(
                                                    text = task.priority.uppercase(),
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = when (task.priority) {
                                                            "High" -> MindError
                                                            "Medium" -> MindWarning
                                                            else -> MindSuccess
                                                        },
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 9.sp
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Text(
                                                text = "Due: ${task.deadline}",
                                                style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 11.sp)
                                            )
                                        }
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.deleteTask(task) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Slate500, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                } else {
                    // Reminders Tab
                    items(reminders) { reminder ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = DarkNavyCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.toggleReminder(reminder.id, reminder.isCompleted) }
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
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
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(CyanAccent.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = if (reminder.isLocationBased) "📍" else "⏰", fontSize = 18.sp)
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            text = reminder.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = Slate100,
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                        Text(
                                            text = "${reminder.timeFormatted} • ${if (reminder.locationName.isNotBlank()) reminder.locationName else reminder.dateFormatted}",
                                            style = MaterialTheme.typography.labelSmall.copy(color = CyanAccent)
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.deleteReminder(reminder) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Slate500, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Task Modal
        if (showAddTaskDialog) {
            AlertDialog(
                onDismissRequest = { showAddTaskDialog = false },
                title = { Text("Add Task", color = Slate100, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        TextField(
                            value = newTaskTitle,
                            onValueChange = { newTaskTitle = it },
                            placeholder = { Text("e.g. Finish 3D prototype render") },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Slate800,
                                unfocusedContainerColor = Slate800,
                                focusedTextColor = Slate100,
                                unfocusedTextColor = Slate100
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("High", "Medium", "Low").forEach { p ->
                                FilterChip(
                                    selected = newTaskPriority == p,
                                    onClick = { newTaskPriority = p },
                                    label = { Text(p) }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newTaskTitle.isNotBlank()) {
                                viewModel.addTask(newTaskTitle, priority = newTaskPriority, deadline = newTaskDeadline)
                                newTaskTitle = ""
                                showAddTaskDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Slate900)
                    ) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddTaskDialog = false }) { Text("Cancel", color = Slate400) }
                },
                containerColor = DarkNavyCard
            )
        }

        // Add Reminder Modal
        if (showAddReminderDialog) {
            AlertDialog(
                onDismissRequest = { showAddReminderDialog = false },
                title = { Text("Set Reminder", color = Slate100, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        TextField(
                            value = newReminderTitle,
                            onValueChange = { newReminderTitle = it },
                            placeholder = { Text("e.g. Buy foam board") },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Slate800,
                                unfocusedContainerColor = Slate800,
                                focusedTextColor = Slate100,
                                unfocusedTextColor = Slate100
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        TextField(
                            value = newReminderTime,
                            onValueChange = { newReminderTime = it },
                            placeholder = { Text("Time (e.g. 5:00 PM)") },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Slate800,
                                unfocusedContainerColor = Slate800,
                                focusedTextColor = Slate100,
                                unfocusedTextColor = Slate100
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newReminderTitle.isNotBlank()) {
                                viewModel.addReminder(newReminderTitle, time = newReminderTime)
                                newReminderTitle = ""
                                showAddReminderDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Slate900)
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddReminderDialog = false }) { Text("Cancel", color = Slate400) }
                },
                containerColor = DarkNavyCard
            )
        }
    }
}
