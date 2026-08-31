package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.VoiceOrbState
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MindOrb(
    state: VoiceOrbState = VoiceOrbState.IDLE,
    primaryColor: Color = Color(0xFF00E5FF),
    accentColor: Color = Color(0xFF2563EB),
    size: Dp = 160.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "OrbTransition")

    // Pulsing scale
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = when (state) {
            VoiceOrbState.LISTENING -> 0.92f
            VoiceOrbState.SPEAKING -> 0.88f
            VoiceOrbState.THINKING -> 0.95f
            else -> 0.97f
        },
        targetValue = when (state) {
            VoiceOrbState.LISTENING -> 1.08f
            VoiceOrbState.SPEAKING -> 1.15f
            VoiceOrbState.THINKING -> 1.05f
            else -> 1.03f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (state) {
                    VoiceOrbState.SPEAKING -> 600
                    VoiceOrbState.LISTENING -> 900
                    VoiceOrbState.THINKING -> 400
                    else -> 1800
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    // Rotation for thinking state
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (state == VoiceOrbState.THINKING) 1500 else 8000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrbRotation"
    )

    // Glow alpha
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowAlpha"
    )

    val coreColors = when (state) {
        VoiceOrbState.ERROR -> listOf(Color(0xFFEF4444), Color(0xFF991B1B))
        VoiceOrbState.THINKING -> listOf(Color(0xFF38BDF8), Color(0xFF818CF8), Color(0xFF00E5FF))
        VoiceOrbState.SPEAKING -> listOf(primaryColor, Color(0xFF67E8F9), accentColor)
        VoiceOrbState.LISTENING -> listOf(primaryColor, Color(0xFF06B6D4), Color(0xFF3B82F6))
        VoiceOrbState.IDLE -> listOf(primaryColor, accentColor, Color(0xFF0F172A))
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val baseRadius = (this.size.minDimension / 2f) * 0.7f * pulseScale

            // 1. Outer ambient glow halo
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        coreColors.first().copy(alpha = glowAlpha * 0.45f),
                        coreColors.getOrElse(1) { coreColors.first() }.copy(alpha = glowAlpha * 0.15f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = baseRadius * 1.5f
                ),
                center = center,
                radius = baseRadius * 1.5f
            )

            // 2. Dynamic orbital energy rings
            val ringCount = if (state == VoiceOrbState.SPEAKING || state == VoiceOrbState.THINKING) 3 else 2
            for (i in 0 until ringCount) {
                val ringOffset = (i * 45f) + rotationAngle
                val rad = Math.toRadians(ringOffset.toDouble())
                val ringCenter = Offset(
                    center.x + (cos(rad) * (baseRadius * 0.15f)).toFloat(),
                    center.y + (sin(rad) * (baseRadius * 0.15f)).toFloat()
                )

                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            coreColors.first().copy(alpha = 0.6f),
                            coreColors.getOrElse(1) { primaryColor }.copy(alpha = 0.2f),
                            coreColors.first().copy(alpha = 0.8f)
                        ),
                        center = ringCenter
                    ),
                    center = ringCenter,
                    radius = baseRadius * (0.9f + (i * 0.08f)),
                    style = Stroke(width = 2.5.dp.toPx())
                )
            }

            // 3. Core glowing sphere
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.9f),
                        coreColors.first(),
                        coreColors.getOrElse(1) { accentColor },
                        Color(0xFF050B18).copy(alpha = 0.85f)
                    ),
                    center = Offset(center.x - baseRadius * 0.2f, center.y - baseRadius * 0.25f),
                    radius = baseRadius
                ),
                center = center,
                radius = baseRadius
            )

            // 4. Central highlight glint
            drawCircle(
                color = Color.White.copy(alpha = 0.75f),
                center = Offset(center.x - baseRadius * 0.35f, center.y - baseRadius * 0.35f),
                radius = baseRadius * 0.18f
            )
        }
    }
}
