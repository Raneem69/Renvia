package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ui.AppScreen
import com.example.ui.MindViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MindTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MindViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settings by viewModel.settings.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val smartCaptureOpen by viewModel.smartCaptureModalOpen.collectAsState()

            MindTheme(
                aesthetic = settings.themeAesthetic,
                fontStyle = settings.fontStyle
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(settings.themeAesthetic.backgroundColorHex)
                ) {
                    Crossfade(
                        targetState = currentScreen,
                        label = "ScreenTransition"
                    ) { screen ->
                        when (screen) {
                            AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                            AppScreen.CHAT -> ChatScreen(viewModel = viewModel)
                            AppScreen.TALK_TO_MIND -> TalkToMindScreen(viewModel = viewModel)
                            AppScreen.TODAY_TASKS -> TodayTasksScreen(viewModel = viewModel)
                            AppScreen.MEMORY -> MemoryScreen(viewModel = viewModel)
                            AppScreen.TOOLS -> ToolsScreen(viewModel = viewModel)
                            AppScreen.HEALTH -> HealthFitnessScreen(viewModel = viewModel)
                            AppScreen.SETTINGS, AppScreen.AESTHETICS_STORE, AppScreen.PRO_UPGRADE -> SettingsScreen(viewModel = viewModel)
                        }
                    }

                    if (smartCaptureOpen) {
                        SmartCaptureDialog(
                            viewModel = viewModel,
                            onDismiss = { viewModel.closeSmartCapture() }
                        )
                    }
                }
            }
        }
    }
}
