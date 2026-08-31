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
import com.example.data.FontStyleFamily
import com.example.data.ThemeAesthetic
import com.example.ui.AppScreen
import com.example.ui.MindViewModel
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    viewModel: MindViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()

    var assistantNameInput by remember(settings.assistantName) { mutableStateOf(settings.assistantName) }
    var userNameInput by remember(settings.userName) { mutableStateOf(settings.userName) }
    var customInstructionsInput by remember(settings.customInstructions) { mutableStateOf(settings.customInstructions) }

    val themes = ThemeAesthetic.values()
    val fontStyles = FontStyleFamily.values()
    val languages = listOf("English (US)", "Egyptian Arabic (اللهجة المصرية)", "Standard Arabic (العربية)", "French", "Spanish", "German")
    val voices = listOf("Aura (Warm & Calm)", "Nova (Energetic)", "Orion (Deep & Confident)", "Echo (Balanced)", "Shimmer (Bright)")

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
                        .testTag("settings_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Slate100)
                }

                Text(
                    text = "SETTINGS & PREFERENCES",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate100,
                        letterSpacing = 1.5.sp
                    )
                )

                Surface(
                    shape = CircleShape,
                    color = CyanAccent.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "V 2.5",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CyanAccent,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // --- SECTION 1: PRO UPGRADE & REGIONAL PRICING ---
                item {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = DarkNavyCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (settings.isPro) MindPurple else CyanAccent.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(text = "👑", fontSize = 20.sp)
                                    Text(
                                        text = "MIND Pro",
                                        style = MaterialTheme.typography.titleMedium.copy(color = Slate100, fontWeight = FontWeight.Bold)
                                    )
                                }
                                Switch(
                                    checked = settings.isPro,
                                    onCheckedChange = { viewModel.togglePro(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Slate900,
                                        checkedTrackColor = CyanAccent
                                    )
                                )
                            }

                            Text(
                                text = if (settings.billingRegion == "Egypt") "Egyptian Regional Rate: 60 EGP / month" else "$10.00 / month",
                                style = MaterialTheme.typography.bodySmall.copy(color = CyanAccent, fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "Unlocks unlimited Deep Research, Image & Video generation, custom themes, and unlimited audio sessions.",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate400, fontSize = 11.sp)
                            )
                        }
                    }
                }

                // --- SECTION 2: ASSISTANT IDENTITY ---
                item {
                    SettingsSection(title = "ASSISTANT IDENTITY") {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(text = "Assistant Name", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextField(
                                    value = assistantNameInput,
                                    onValueChange = {
                                        assistantNameInput = it
                                        viewModel.updateAssistantName(it)
                                    },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Slate800,
                                        unfocusedContainerColor = Slate800,
                                        focusedTextColor = Slate100,
                                        unfocusedTextColor = Slate100
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // --- SECTION 3: USER PROFILE & ABOUT ME ---
                item {
                    SettingsSection(title = "USER PROFILE & CONTEXT") {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(text = "Your Name", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                            TextField(
                                value = userNameInput,
                                onValueChange = {
                                    userNameInput = it
                                    viewModel.updateProfile(it, settings.userGender, settings.userAge, settings.interests, settings.hobbies)
                                },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Slate800,
                                    unfocusedContainerColor = Slate800,
                                    focusedTextColor = Slate100,
                                    unfocusedTextColor = Slate100
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(
                                text = "Interests: Interior Design, Nordic Furniture, Winter Palettes, Architecture",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate300)
                            )
                        }
                    }
                }

                // --- SECTION 4: THEME AESTHETICS STORE (11 Themes) ---
                item {
                    SettingsSection(title = "MIND AESTHETICS STORE") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Active Theme: ${settings.themeAesthetic.displayName}",
                                style = MaterialTheme.typography.bodySmall.copy(color = CyanAccent, fontWeight = FontWeight.Bold)
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                contentPadding = PaddingValues(vertical = 4.dp)
                            ) {
                                items(themes) { aesthetic ->
                                    val isSelected = settings.themeAesthetic == aesthetic
                                    Surface(
                                        shape = RoundedCornerShape(18.dp),
                                        color = Color(aesthetic.surfaceColorHex),
                                        border = androidx.compose.foundation.BorderStroke(
                                            2.dp,
                                            if (isSelected) CyanAccent else Slate700
                                        ),
                                        modifier = Modifier
                                            .size(width = 110.dp, height = 75.dp)
                                            .clickable { viewModel.updateAesthetic(aesthetic) }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(8.dp),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color(aesthetic.primaryColorHex)))
                                                Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color(aesthetic.accentColorHex)))
                                            }
                                            Text(
                                                text = aesthetic.displayName,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Color(aesthetic.textColorHex),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                ),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // --- SECTION 5: TYPOGRAPHY & FONT ---
                item {
                    SettingsSection(title = "TYPOGRAPHY & ACCESSIBILITY") {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(fontStyles) { font ->
                                val isSelected = settings.fontStyle == font
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.updateFont(font) },
                                    label = { Text(font.displayName) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyanAccent,
                                        selectedLabelColor = Slate900
                                    )
                                )
                            }
                        }
                    }
                }

                // --- SECTION 6: LANGUAGE & LOCALIZATION ---
                item {
                    SettingsSection(title = "AI LANGUAGE & DIALECT") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            languages.forEach { lang ->
                                val isSelected = settings.aiLanguage.contains(lang.take(8))
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = if (isSelected) CyanAccent.copy(alpha = 0.15f) else Slate800,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CyanAccent else Slate700),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.updateLanguages(settings.appLanguage, lang) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = lang, style = MaterialTheme.typography.bodySmall.copy(color = Slate100))
                                        if (isSelected) {
                                            Icon(Icons.Default.Check, contentDescription = "Selected", tint = CyanAccent, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // --- SECTION 7: MUSIC INTEGRATIONS ---
                item {
                    SettingsSection(title = "CONNECTED SERVICES") {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(text = "🎵", fontSize = 18.sp)
                                    Text(text = "Spotify Integration", style = MaterialTheme.typography.bodySmall.copy(color = Slate100))
                                }
                                Switch(
                                    checked = settings.spotifyConnected,
                                    onCheckedChange = { viewModel.toggleSpotify() },
                                    colors = SwitchDefaults.colors(checkedTrackColor = CyanAccent, checkedThumbColor = Slate900)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(text = "🍎", fontSize = 18.sp)
                                    Text(text = "Apple Music", style = MaterialTheme.typography.bodySmall.copy(color = Slate100))
                                }
                                Switch(
                                    checked = settings.appleMusicConnected,
                                    onCheckedChange = { viewModel.toggleAppleMusic() },
                                    colors = SwitchDefaults.colors(checkedTrackColor = CyanAccent, checkedThumbColor = Slate900)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = DarkNavyCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Slate400,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
            content()
        }
    }
}
