package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.MindViewModel
import com.example.ui.theme.*

@Composable
fun HealthFitnessScreen(
    viewModel: MindViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    val health by viewModel.healthMetric.collectAsState()

    val steps = health?.steps ?: 6482
    val goal = health?.stepGoal ?: 10000
    val progress = (steps.toFloat() / goal.toFloat()).coerceIn(0f, 1f)
    val distanceKm = health?.distanceKm ?: 4.8f
    val calories = health?.caloriesBurned ?: 340
    val waterMl = health?.waterMl ?: 1500
    val sleepHours = health?.sleepHours ?: 7.2f

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
                    text = "HEALTH & ACTIVITY",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Slate100,
                        letterSpacing = 2.sp
                    )
                )

                Surface(
                    shape = CircleShape,
                    color = MindSuccess.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "OPT-IN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MindSuccess,
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
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Steps Ring Card
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(32.dp))
                            .background(DarkNavyCard)
                            .border(1.dp, Slate700.copy(alpha = 0.5f), RoundedCornerShape(32.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "TODAY'S STEP GOAL",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Slate400,
                                    letterSpacing = 2.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Text(
                                text = "$steps",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CyanAccent,
                                    fontSize = 44.sp
                                )
                            )

                            Text(
                                text = "of $goal steps (${(progress * 100).toInt()}%)",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Slate300)
                            )

                            // Linear Progress Bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape)
                                    .background(Slate800)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(progress)
                                        .fillMaxHeight()
                                        .clip(CircleShape)
                                        .background(CyanAccent)
                                )
                            }

                            Button(
                                onClick = { viewModel.addSteps(500) },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = Slate900)
                            ) {
                                Text("+500 Steps Walked", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // 2-Column Metrics (Distance, Calories, Sleep, Water)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Distance
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = DarkNavyCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(text = "📍 Distance", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                                Text(text = "${String.format("%.1f", distanceKm)} km", style = MaterialTheme.typography.titleLarge.copy(color = Slate100, fontWeight = FontWeight.Bold))
                            }
                        }

                        // Calories
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = DarkNavyCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(text = "🔥 Calories", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                                Text(text = "$calories kcal", style = MaterialTheme.typography.titleLarge.copy(color = MindWarning, fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Sleep
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = DarkNavyCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(text = "🌙 Sleep", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                                Text(text = "$sleepHours hrs", style = MaterialTheme.typography.titleLarge.copy(color = MindPurple, fontWeight = FontWeight.Bold))
                            }
                        }

                        // Water
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = DarkNavyCard,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.addWater(250) }
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "💧 Water", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                                    Text(text = "+250ml", style = MaterialTheme.typography.labelSmall.copy(color = CyanAccent, fontWeight = FontWeight.Bold))
                                }
                                Text(text = "${waterMl} ml", style = MaterialTheme.typography.titleLarge.copy(color = CyanAccent, fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }
            }
        }
    }
}
