package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiAssistantService
import com.example.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppScreen {
    HOME,
    CHAT,
    TALK_TO_MIND,
    TODAY_TASKS,
    MEMORY,
    TOOLS,
    HEALTH,
    SETTINGS,
    AESTHETICS_STORE,
    PRO_UPGRADE
}

enum class VoiceOrbState {
    IDLE,
    LISTENING,
    THINKING,
    SPEAKING,
    ERROR
}

data class SmartCaptureResult(
    val type: String, // "screenshot", "receipt", "document", "voice", "image", "link"
    val summaryText: String,
    val actionType: String? = null,
    val actionPayload: Map<String, String>? = null
)

class MindViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val taskDao = db.taskDao()
    private val reminderDao = db.reminderDao()
    private val memoryDao = db.memoryDao()
    private val conversationDao = db.conversationDao()
    private val chatMessageDao = db.chatMessageDao()
    private val healthMetricDao = db.healthMetricDao()

    private val prefsRepo = UserPreferencesRepository(application)
    private val aiService = GeminiAssistantService()

    val settings: StateFlow<UserSettingsState> = prefsRepo.settings
    val allTasks: StateFlow<List<TaskEntity>> = taskDao.getAllTasks().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allReminders: StateFlow<List<ReminderEntity>> = reminderDao.getAllReminders().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allMemories: StateFlow<List<MemoryEntity>> = memoryDao.getAllMemories().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allConversations: StateFlow<List<ConversationEntity>> = conversationDao.getAllConversations().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val healthMetric: StateFlow<HealthMetricEntity?> = healthMetricDao.getMetricForDate("today").stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _activeConversationId = MutableStateFlow("conv_default_1")
    val activeConversationId: StateFlow<String> = _activeConversationId.asStateFlow()

    private val _activeMessages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val activeMessages: StateFlow<List<ChatMessageEntity>> = _activeMessages.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _voiceOrbState = MutableStateFlow(VoiceOrbState.IDLE)
    val voiceOrbState: StateFlow<VoiceOrbState> = _voiceOrbState.asStateFlow()

    private val _voiceTranscript = MutableStateFlow("")
    val voiceTranscript: StateFlow<String> = _voiceTranscript.asStateFlow()

    private val _voiceAssistantSpokenText = MutableStateFlow("")
    val voiceAssistantSpokenText: StateFlow<String> = _voiceAssistantSpokenText.asStateFlow()

    private val _smartCaptureModalOpen = MutableStateFlow(false)
    val smartCaptureModalOpen: StateFlow<Boolean> = _smartCaptureModalOpen.asStateFlow()

    private val _smartCaptureResult = MutableStateFlow<SmartCaptureResult?>(null)
    val smartCaptureResult: StateFlow<SmartCaptureResult?> = _smartCaptureResult.asStateFlow()

    private val _memorySearchQuery = MutableStateFlow("")
    val memorySearchQuery: StateFlow<String> = _memorySearchQuery.asStateFlow()

    private var activeGenerationJob: Job? = null

    init {
        viewModelScope.launch {
            _activeConversationId.collect { convId ->
                chatMessageDao.getMessagesForConversation(convId).collect { msgs ->
                    _activeMessages.value = msgs
                }
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun navigateBack() {
        if (_currentScreen.value != AppScreen.HOME) {
            _currentScreen.value = AppScreen.HOME
        }
    }

    // --- Task Actions ---
    fun addTask(title: String, description: String = "", priority: String = "Medium", deadline: String = "Today") {
        viewModelScope.launch {
            taskDao.insertTask(
                TaskEntity(
                    title = title,
                    description = description,
                    priority = priority,
                    deadline = deadline
                )
            )
        }
    }

    fun toggleTask(id: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            taskDao.setTaskCompleted(id, !currentStatus)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            taskDao.deleteTask(task)
        }
    }

    // --- Reminder Actions ---
    fun addReminder(title: String, time: String = "5:00 PM", date: String = "Today", isLocation: Boolean = false, location: String = "") {
        viewModelScope.launch {
            reminderDao.insertReminder(
                ReminderEntity(
                    title = title,
                    timeFormatted = time,
                    dateFormatted = date,
                    isLocationBased = isLocation,
                    locationName = location
                )
            )
        }
    }

    fun toggleReminder(id: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            reminderDao.setReminderCompleted(id, !currentStatus)
        }
    }

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            reminderDao.deleteReminder(reminder)
        }
    }

    // --- Memory Actions ---
    fun addMemory(content: String, category: String = "Personal") {
        viewModelScope.launch {
            memoryDao.insertMemory(
                MemoryEntity(
                    content = content,
                    category = category,
                    isPinned = false
                )
            )
        }
    }

    fun deleteMemory(memory: MemoryEntity) {
        viewModelScope.launch {
            memoryDao.deleteMemory(memory)
        }
    }

    fun deleteAllMemories() {
        viewModelScope.launch {
            memoryDao.deleteAllMemories()
        }
    }

    fun updateMemorySearch(query: String) {
        _memorySearchQuery.value = query
    }

    // --- Conversation & Chat Actions ---
    fun startNewChat() {
        viewModelScope.launch {
            val newId = "conv_${UUID.randomUUID()}"
            val newConv = ConversationEntity(
                id = newId,
                title = "New Conversation",
                updatedAt = System.currentTimeMillis()
            )
            conversationDao.insertConversation(newConv)
            _activeConversationId.value = newId
            _currentScreen.value = AppScreen.CHAT
        }
    }

    fun selectConversation(id: String) {
        _activeConversationId.value = id
        _currentScreen.value = AppScreen.CHAT
    }

    fun deleteConversation(id: String) {
        viewModelScope.launch {
            conversationDao.deleteConversation(id)
            chatMessageDao.deleteMessagesForConversation(id)
            if (_activeConversationId.value == id) {
                val remaining = allConversations.value.filter { it.id != id }
                if (remaining.isNotEmpty()) {
                    _activeConversationId.value = remaining.first().id
                } else {
                    startNewChat()
                }
            }
        }
    }

    fun renameConversation(id: String, newTitle: String) {
        viewModelScope.launch {
            val conv = conversationDao.getConversationById(id)
            if (conv != null) {
                conversationDao.updateConversation(conv.copy(title = newTitle))
            }
        }
    }

    fun sendMessage(userText: String, toolType: String? = null, imageUri: String? = null) {
        if (userText.isBlank() && imageUri == null) return

        val convId = _activeConversationId.value
        val userMsg = ChatMessageEntity(
            conversationId = convId,
            sender = "user",
            text = userText,
            timestamp = System.currentTimeMillis(),
            toolType = toolType,
            imageAttachmentUri = imageUri
        )

        viewModelScope.launch {
            chatMessageDao.insertMessage(userMsg)

            // Update conversation title if first message
            val currentMsgs = _activeMessages.value
            if (currentMsgs.isEmpty() || currentMsgs.size <= 1) {
                val titleWords = userText.take(28)
                val newTitle = if (titleWords.length >= 28) "$titleWords..." else titleWords
                renameConversation(convId, newTitle.ifBlank { "Conversation" })
            }

            _isGenerating.value = true

            activeGenerationJob = launch {
                val historyPairs = currentMsgs.map { it.sender to it.text }
                val memoriesList = allMemories.value.map { it.content }

                val aiResult = aiService.generateResponse(
                    prompt = userText,
                    conversationHistory = historyPairs,
                    userSettings = settings.value,
                    activeMemories = memoriesList,
                    toolType = toolType
                )

                _isGenerating.value = false

                // Insert assistant message
                val assistantMsg = ChatMessageEntity(
                    conversationId = convId,
                    sender = "assistant",
                    text = aiResult.text,
                    timestamp = System.currentTimeMillis(),
                    sources = if (aiResult.sources.isNotEmpty()) aiResult.sources.joinToString(" • ") else null
                )
                chatMessageDao.insertMessage(assistantMsg)

                // Execute parsed actions if any
                aiResult.actionType?.let { action ->
                    when (action) {
                        "ADD_TASK" -> {
                            val title = aiResult.actionPayload?.get("title") ?: "New Task"
                            val deadline = aiResult.actionPayload?.get("deadline") ?: "Today"
                            addTask(title = title, deadline = deadline)
                        }
                        "ADD_REMINDER" -> {
                            val title = aiResult.actionPayload?.get("title") ?: "Reminder"
                            val time = aiResult.actionPayload?.get("time") ?: "5:00 PM"
                            addReminder(title = title, time = time)
                        }
                        "SAVE_MEMORY" -> {
                            val content = aiResult.actionPayload?.get("content") ?: ""
                            if (content.isNotBlank()) {
                                addMemory(content = content)
                            }
                        }
                    }
                }
            }
        }
    }

    fun stopGeneration() {
        activeGenerationJob?.cancel()
        _isGenerating.value = false
    }

    // --- Smart Capture Actions ---
    fun openSmartCapture() {
        _smartCaptureModalOpen.value = true
        _smartCaptureResult.value = null
    }

    fun closeSmartCapture() {
        _smartCaptureModalOpen.value = false
        _smartCaptureResult.value = null
    }

    fun triggerSmartCapture(type: String, content: String) {
        viewModelScope.launch {
            _isGenerating.value = true
            val response = aiService.analyzeSmartCapture(type, content, settings.value)
            _isGenerating.value = false
            _smartCaptureResult.value = SmartCaptureResult(
                type = type,
                summaryText = response.text,
                actionType = response.actionType,
                actionPayload = response.actionPayload
            )
        }
    }

    fun confirmCaptureAction() {
        val res = _smartCaptureResult.value ?: return
        when (res.actionType) {
            "ADD_TASK" -> {
                val title = res.actionPayload?.get("title") ?: "Captured Task"
                val deadline = res.actionPayload?.get("deadline") ?: "Today"
                addTask(title, deadline = deadline)
            }
            "ADD_REMINDER" -> {
                val title = res.actionPayload?.get("title") ?: "Captured Reminder"
                val time = res.actionPayload?.get("time") ?: "5:00 PM"
                addReminder(title, time = time)
            }
            "SAVE_MEMORY" -> {
                val content = res.actionPayload?.get("content") ?: res.summaryText
                addMemory(content)
            }
        }
        closeSmartCapture()
    }

    // --- Real-Time Voice Conversation Simulation ---
    fun startVoiceSession() {
        _currentScreen.value = AppScreen.TALK_TO_MIND
        _voiceOrbState.value = VoiceOrbState.LISTENING
        _voiceTranscript.value = "Listening to you..."
        _voiceAssistantSpokenText.value = ""
    }

    fun simulateVoiceUserSpeech(userSpeech: String) {
        _voiceOrbState.value = VoiceOrbState.THINKING
        _voiceTranscript.value = "You: \"$userSpeech\""

        viewModelScope.launch {
            delay(1200)
            val memoriesList = allMemories.value.map { it.content }
            val response = aiService.generateResponse(
                prompt = userSpeech,
                userSettings = settings.value,
                activeMemories = memoriesList
            )
            _voiceOrbState.value = VoiceOrbState.SPEAKING
            _voiceAssistantSpokenText.value = response.text
            
            // Allow speaking state for animation
            delay(4000)
            if (_voiceOrbState.value == VoiceOrbState.SPEAKING) {
                _voiceOrbState.value = VoiceOrbState.LISTENING
                _voiceTranscript.value = "Listening..."
            }
        }
    }

    fun endVoiceSession() {
        _voiceOrbState.value = VoiceOrbState.IDLE
        _currentScreen.value = AppScreen.HOME
    }

    // --- Health Step Increment / Simulation ---
    fun addSteps(amount: Int = 250) {
        viewModelScope.launch {
            val current = healthMetric.value ?: HealthMetricEntity("today")
            val updated = current.copy(
                steps = current.steps + amount,
                distanceKm = ((current.steps + amount) * 0.00075f),
                caloriesBurned = current.caloriesBurned + 12
            )
            healthMetricDao.insertOrUpdate(updated)
        }
    }

    fun addWater(amountMl: Int = 250) {
        viewModelScope.launch {
            val current = healthMetric.value ?: HealthMetricEntity("today")
            val updated = current.copy(waterMl = current.waterMl + amountMl)
            healthMetricDao.insertOrUpdate(updated)
        }
    }

    // --- Settings Delegates ---
    fun updateAssistantName(name: String) = prefsRepo.updateAssistantName(name)
    fun updateProfile(name: String, gender: String, age: Int, interests: Set<String>, hobbies: Set<String>) =
        prefsRepo.updateProfile(name, gender, age, interests, hobbies)
    fun updatePersonality(personalities: Set<String>, responseStyle: String, commStyle: String, useEmojis: Boolean, customInstructions: String) =
        prefsRepo.updatePersonality(personalities, responseStyle, commStyle, useEmojis, customInstructions)
    fun updateLanguages(appLang: String, aiLang: String) = prefsRepo.updateLanguages(appLang, aiLang)
    fun updateAesthetic(aesthetic: ThemeAesthetic) = prefsRepo.updateAesthetic(aesthetic)
    fun updateFont(font: FontStyleFamily) = prefsRepo.updateFont(font)
    fun toggleTempUnit() = prefsRepo.toggleTempUnit()
    fun togglePro(isPro: Boolean) = prefsRepo.togglePro(isPro)
    fun setBillingRegion(region: String) = prefsRepo.setBillingRegion(region)
    fun toggleSpotify() = prefsRepo.toggleSpotify()
    fun toggleAppleMusic() = prefsRepo.toggleAppleMusic()
    fun toggleMemory(enabled: Boolean) = prefsRepo.toggleMemory(enabled)
}
