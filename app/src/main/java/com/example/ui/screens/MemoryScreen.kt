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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MemoryEntity
import com.example.ui.AppScreen
import com.example.ui.MindViewModel
import com.example.ui.theme.*

@Composable
fun MemoryScreen(
    viewModel: MindViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    val memories by viewModel.allMemories.collectAsState()
    val searchQuery by viewModel.memorySearchQuery.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var newMemoryText by remember { mutableStateOf("") }
    var newMemoryCategory by remember { mutableStateOf("Project") }

    val filteredMemories = if (searchQuery.isBlank()) {
        memories
    } else {
        memories.filter { it.content.contains(searchQuery, ignoreCase = true) }
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
                    text = "SAVED MEMORY",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate100,
                        letterSpacing = 2.sp
                    )
                )

                IconButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CyanAccent)
                        .testTag("add_memory_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Memory", tint = Slate900)
                }
            }

            // Memory Explanation & Privacy Notice
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = DarkNavyCard.copy(alpha = 0.7f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate700.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "🔒", fontSize = 20.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Explicit Long-Term Memory",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Slate100,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "MIND only remembers facts you explicitly save. Conversation history is stored separately.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate400, fontSize = 11.sp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            TextField(
                value = searchQuery,
                onValueChange = { viewModel.updateMemorySearch(it) },
                placeholder = { Text("Search your memories...", style = MaterialTheme.typography.bodySmall.copy(color = Slate500)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Slate400) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = DarkNavyCard,
                    unfocusedContainerColor = DarkNavyCard,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Slate100,
                    unfocusedTextColor = Slate100
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Slate700.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Memory List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                if (filteredMemories.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (searchQuery.isBlank()) "No memories saved yet. Tap + to add one!" else "No matching memories found.",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Slate400)
                            )
                        }
                    }
                }

                items(filteredMemories) { memory ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = DarkNavyCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (memory.isPinned) CyanAccent.copy(alpha = 0.3f) else Slate800),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Surface(
                                    shape = CircleShape,
                                    color = CyanAccent.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = memory.category.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CyanAccent,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = memory.content,
                                    style = MaterialTheme.typography.bodyMedium.copy(color = Slate100)
                                )
                            }

                            IconButton(
                                onClick = { viewModel.deleteMemory(memory) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Slate400, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // Add Memory Modal
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add New Memory", color = Slate100, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        TextField(
                            value = newMemoryText,
                            onValueChange = { newMemoryText = it },
                            placeholder = { Text("e.g. My project uses winter palette") },
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
                            if (newMemoryText.isNotBlank()) {
                                viewModel.addMemory(newMemoryText, newMemoryCategory)
                                newMemoryText = ""
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Slate900)
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) { Text("Cancel", color = Slate400) }
                },
                containerColor = DarkNavyCard
            )
        }
    }
}
