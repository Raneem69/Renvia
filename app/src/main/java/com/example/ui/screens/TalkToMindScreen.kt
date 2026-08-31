package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MindViewModel
import com.example.ui.VoiceOrbState
import com.example.ui.components.MindOrb
import com.example.ui.theme.*

@Composable
fun TalkToMindScreen(
    viewModel: MindViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.settings.collectAsState()
    val orbState by viewModel.voiceOrbState.collectAsState()
    val transcript by viewModel.voiceTranscript.collectAsState()
    val assistantSpokenText by viewModel.voiceAssistantSpokenText.collectAsState()

    var isMuted by remember { mutableStateOf(false) }
    var isSpeakerOn by remember { mutableStateOf(true) }

    val statusLabel = when (orbState) {
        VoiceOrbState.LISTENING -> "Listening..."
        VoiceOrbState.THINKING -> "${settings.assistantName} is thinking..."
        VoiceOrbState.SPEAKING -> "${settings.assistantName} is speaking..."
        VoiceOrbState.ERROR -> "Connection issue"
        VoiceOrbState.IDLE -> "Tap orb or choose a topic"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(settings.themeAesthetic.backgroundColorHex))
    ) {
        // Ambient glow
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.Center)
                .clip(CircleShape)
                .background(CyanAccent.copy(alpha = 0.12f))
                .blur(90.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // --- TOP HEADER ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.endVoiceSession() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Slate800.copy(alpha = 0.6f))
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate100)
                }

                Surface(
                    shape = CircleShape,
                    color = Slate800.copy(alpha = 0.8f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate700)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "🎙️", fontSize = 14.sp)
                        Text(
                            text = settings.voiceId,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Slate200,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = if (isSpeakerOn) CyanAccent.copy(alpha = 0.2f) else Slate800,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { isSpeakerOn = !isSpeakerOn }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            if (isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Speaker",
                            tint = if (isSpeakerOn) CyanAccent else Slate400,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // --- CENTER ORB & STATUS ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = settings.assistantName,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Slate100,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                )

                Text(
                    text = statusLabel,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = CyanAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // The Central Animated Mind AI Orb
                Box(
                    modifier = Modifier.clickable {
                        if (orbState == VoiceOrbState.IDLE || orbState == VoiceOrbState.LISTENING) {
                            viewModel.simulateVoiceUserSpeech("What are my key deadlines for this week?")
                        }
                    }
                ) {
                    MindOrb(
                        state = orbState,
                        primaryColor = Color(settings.themeAesthetic.primaryColorHex),
                        accentColor = Color(settings.themeAesthetic.accentColorHex),
                        size = 200.dp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Live Spoken Transcription Bubble
                if (assistantSpokenText.isNotBlank() || transcript.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = DarkNavyCard.copy(alpha = 0.85f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.25f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (transcript.isNotBlank()) {
                                Text(
                                    text = transcript,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Slate400,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                )
                            }
                            if (assistantSpokenText.isNotBlank()) {
                                Text(
                                    text = assistantSpokenText,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Slate100,
                                        lineHeight = 20.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // --- BOTTOM CONTROLS & SUGGESTION CHIPS ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Quick voice prompt chips
                val voiceChips = listOf(
                    "What's on my schedule today?",
                    "Remind me to buy foam board",
                    "How is the weather tonight?",
                    "Explain winter palette in Arabic"
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(voiceChips) { prompt ->
                        Surface(
                            shape = CircleShape,
                            color = Slate800.copy(alpha = 0.7f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                            modifier = Modifier.clickable {
                                viewModel.simulateVoiceUserSpeech(prompt)
                            }
                        ) {
                            Text(
                                text = prompt,
                                style = MaterialTheme.typography.labelSmall.copy(color = Slate200),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                // Action Buttons: Mute, Push To Talk, End Call
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute
                    IconButton(
                        onClick = { isMuted = !isMuted },
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(if (isMuted) MindError.copy(alpha = 0.2f) else Slate800)
                            .border(1.dp, if (isMuted) MindError else Slate700, CircleShape)
                    ) {
                        Icon(
                            if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute",
                            tint = if (isMuted) MindError else Slate100,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // End Call
                    IconButton(
                        onClick = { viewModel.endVoiceSession() },
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MindError)
                            .testTag("end_voice_call_button")
                    ) {
                        Icon(
                            Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            tint = PureWhite,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}
