package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeAesthetic(
    val id: String,
    val title: String,
    val description: String,
    val primaryColorHex: Long,
    val backgroundColorHex: Long,
    val surfaceColorHex: Long,
    val accentColorHex: Long,
    val isPro: Boolean = false,
    val textColorHex: Long = 0xFFF1F5F9
) {
    ORIGINAL("original", "MIND Original", "Deep luxury navy + vibrant cyan futuristic design", 0xFF00E5FF, 0xFF050B18, 0xFF0B162C, 0xFF00E5FF, false),
    GOTH("goth", "Goth", "Dark, mysterious and dramatic gothic elegance", 0xFFE0E0E0, 0xFF0A0A0E, 0xFF16161D, 0xFF9E0059, true),
    FLUFFY("fluffy", "Fluffy", "Soft cloud pastel gradient styling", 0xFFFF85A1, 0xFF140D1E, 0xFF241738, 0xFFFFAFCC, false),
    CUTE("cute", "Cute", "Pastel tones and cheerful playful robot expressions", 0xFF38D9A9, 0xFF0B1A24, 0xFF142B3B, 0xFFFFB6C1, false),
    HALLOWEEN("halloween", "Halloween", "Dark orange, obsidian & seasonal mystery", 0xFFFF7700, 0xFF0F0B08, 0xFF1F1610, 0xFFFF9E00, true),
    CHRISTMAS("christmas", "Christmas", "Winter forest greens, rich ruby & festive glow", 0xFF2EC4B6, 0xFF0A1412, 0xFF122421, 0xFFE71D36, true),
    GALAXY("galaxy", "Galaxy", "Cosmic deep space purples & starlight azure", 0xFF7000FF, 0xFF080616, 0xFF140E2E, 0xFF00F0FF, true),
    CYBER("cyber", "Cyberpunk", "High-contrast neon cyan, yellow & dark matrix slate", 0xFF00FFF0, 0xFF050B12, 0xFF0B1E2E, 0xFFFFEE00, true),
    WINTER("winter", "Winter Frosted", "Ice blue, crystalline white and frosted glass", 0xFF80D8FF, 0xFF08121C, 0xFF0F2030, 0xFFE1F5FE, false),
    MINIMAL("minimal", "Minimal Mono", "Pure monochrome slate & refined typography", 0xFFFFFFFF, 0xFF080808, 0xFF141414, 0xFFAAAAAA, false),
    HERO("hero", "Hero Comic", "Bold energetic dynamic comic aesthetic", 0xFFFF3366, 0xFF0A0E1A, 0xFF151C33, 0xFFFFD166, true);

    val displayName: String get() = title
}

enum class FontStyleFamily(val id: String, val title: String, val category: String) {
    MODERN("modern", "Inter Modern", "Clean & geometric"),
    MINIMAL("minimal", "Mono Minimal", "High clarity monospace"),
    ROUNDED("rounded", "Soft Rounded", "Friendly & approachable"),
    ELEGANT("elegant", "Editorial Serif", "Sophisticated & stylish"),
    ACCESSIBILITY("accessibility", "High Readability", "Enhanced contrast & spacing");

    val displayName: String get() = title
}

data class UserSettingsState(
    val assistantName: String = "MIND",
    val userName: String = "Raneem",
    val userGender: String = "Female", // "Female" or "Male"
    val userAge: Int = 22,
    val interests: Set<String> = setOf("Interior Design", "Technology", "Art", "Travel", "Photography"),
    val hobbies: Set<String> = setOf("Design Sketching", "Coffee Brewing", "Exploring Exhibitions"),
    val personalities: Set<String> = setOf("Friendly", "Creative", "Helpful", "Calm"),
    val responseStyle: String = "Balanced", // Short, Balanced, Detailed, Very Detailed
    val communicationStyle: String = "Casual", // Formal, Casual, Professional, Playful, Gen Z
    val useEmojis: Boolean = true,
    val explainReasoning: Boolean = true,
    val askQuestionsFirst: Boolean = false,
    val challengeOpinions: Boolean = false,
    val beProactive: Boolean = true,
    val customInstructions: String = "You are my trusted companion. Tailor advice to design aesthetics, prioritize daily tasks, and respond with warmth and precision.",
    val appLanguage: String = "English",
    val aiLanguage: String = "English", // "English", "Egyptian Arabic", "Arabic", "French", etc.
    val isRtl: Boolean = false,
    val voiceId: String = "Aura - Warm & Calm",
    val voiceSpeed: Float = 1.0f,
    val voicePitch: Float = 1.0f,
    val tempUnitCelsius: Boolean = true,
    val themeAesthetic: ThemeAesthetic = ThemeAesthetic.ORIGINAL,
    val fontStyle: FontStyleFamily = FontStyleFamily.MODERN,
    val isPro: Boolean = false,
    val billingRegion: String = "EGY", // "EGY" or "GLOBAL"
    val homeLocation: String = "New Cairo, Egypt",
    val workLocation: String = "AUC Studio Campus",
    val spotifyConnected: Boolean = true,
    val appleMusicConnected: Boolean = false,
    val memoryEnabled: Boolean = true,
    val enabledWidgets: List<String> = listOf(
        "weather", "tasks", "daily_brief", "quick_actions", "ai_suggestions", "memory", "tools", "steps", "music"
    )
)

class UserPreferencesRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("mind_user_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<UserSettingsState> = _settings.asStateFlow()

    private fun loadSettings(): UserSettingsState {
        val aestheticId = prefs.getString("theme_aesthetic", ThemeAesthetic.ORIGINAL.id) ?: ThemeAesthetic.ORIGINAL.id
        val aesthetic = ThemeAesthetic.values().firstOrNull { it.id == aestheticId } ?: ThemeAesthetic.ORIGINAL

        val fontId = prefs.getString("font_style", FontStyleFamily.MODERN.id) ?: FontStyleFamily.MODERN.id
        val font = FontStyleFamily.values().firstOrNull { it.id == fontId } ?: FontStyleFamily.MODERN

        val aiLang = prefs.getString("ai_language", "English") ?: "English"
        val isRtl = aiLang.contains("Arabic")

        return UserSettingsState(
            assistantName = prefs.getString("assistant_name", "MIND") ?: "MIND",
            userName = prefs.getString("user_name", "Raneem") ?: "Raneem",
            userGender = prefs.getString("user_gender", "Female") ?: "Female",
            userAge = prefs.getInt("user_age", 22),
            interests = prefs.getStringSet("user_interests", setOf("Interior Design", "Technology", "Art", "Travel")) ?: emptySet(),
            hobbies = prefs.getStringSet("user_hobbies", setOf("Design Sketching", "Photography")) ?: emptySet(),
            personalities = prefs.getStringSet("personalities", setOf("Friendly", "Creative", "Helpful")) ?: emptySet(),
            responseStyle = prefs.getString("response_style", "Balanced") ?: "Balanced",
            communicationStyle = prefs.getString("comm_style", "Casual") ?: "Casual",
            useEmojis = prefs.getBoolean("use_emojis", true),
            explainReasoning = prefs.getBoolean("explain_reasoning", true),
            askQuestionsFirst = prefs.getBoolean("ask_questions", false),
            challengeOpinions = prefs.getBoolean("challenge_opinions", false),
            beProactive = prefs.getBoolean("be_proactive", true),
            customInstructions = prefs.getString("custom_instructions", "Be warm, helpful, and concise.") ?: "",
            appLanguage = prefs.getString("app_language", "English") ?: "English",
            aiLanguage = aiLang,
            isRtl = isRtl,
            voiceId = prefs.getString("voice_id", "Aura - Warm & Calm") ?: "Aura - Warm & Calm",
            voiceSpeed = prefs.getFloat("voice_speed", 1.0f),
            voicePitch = prefs.getFloat("voice_pitch", 1.0f),
            tempUnitCelsius = prefs.getBoolean("temp_unit_celsius", true),
            themeAesthetic = aesthetic,
            fontStyle = font,
            isPro = prefs.getBoolean("is_pro", false),
            billingRegion = prefs.getString("billing_region", "EGY") ?: "EGY",
            homeLocation = prefs.getString("home_location", "New Cairo, Egypt") ?: "New Cairo, Egypt",
            workLocation = prefs.getString("work_location", "AUC Studio Campus") ?: "AUC Studio Campus",
            spotifyConnected = prefs.getBoolean("spotify_connected", true),
            appleMusicConnected = prefs.getBoolean("apple_music_connected", false),
            memoryEnabled = prefs.getBoolean("memory_enabled", true)
        )
    }

    fun updateAssistantName(name: String) {
        prefs.edit().putString("assistant_name", name.ifBlank { "MIND" }).apply()
        _settings.value = _settings.value.copy(assistantName = name.ifBlank { "MIND" })
    }

    fun updateProfile(name: String, gender: String, age: Int, interests: Set<String>, hobbies: Set<String>) {
        val safeGender = if (gender == "Male") "Male" else "Female"
        prefs.edit()
            .putString("user_name", name)
            .putString("user_gender", safeGender)
            .putInt("user_age", age)
            .putStringSet("user_interests", interests)
            .putStringSet("user_hobbies", hobbies)
            .apply()
        _settings.value = _settings.value.copy(
            userName = name,
            userGender = safeGender,
            userAge = age,
            interests = interests,
            hobbies = hobbies
        )
    }

    fun updatePersonality(personalities: Set<String>, responseStyle: String, commStyle: String, useEmojis: Boolean, customInstructions: String) {
        prefs.edit()
            .putStringSet("personalities", personalities)
            .putString("response_style", responseStyle)
            .putString("comm_style", commStyle)
            .putBoolean("use_emojis", useEmojis)
            .putString("custom_instructions", customInstructions)
            .apply()
        _settings.value = _settings.value.copy(
            personalities = personalities,
            responseStyle = responseStyle,
            communicationStyle = commStyle,
            useEmojis = useEmojis,
            customInstructions = customInstructions
        )
    }

    fun updateLanguages(appLang: String, aiLang: String) {
        val isRtl = aiLang.contains("Arabic") || appLang.contains("Arabic")
        prefs.edit()
            .putString("app_language", appLang)
            .putString("ai_language", aiLang)
            .apply()
        _settings.value = _settings.value.copy(
            appLanguage = appLang,
            aiLanguage = aiLang,
            isRtl = isRtl
        )
    }

    fun updateAesthetic(aesthetic: ThemeAesthetic) {
        prefs.edit().putString("theme_aesthetic", aesthetic.id).apply()
        _settings.value = _settings.value.copy(themeAesthetic = aesthetic)
    }

    fun updateFont(font: FontStyleFamily) {
        prefs.edit().putString("font_style", font.id).apply()
        _settings.value = _settings.value.copy(fontStyle = font)
    }

    fun toggleTempUnit() {
        val newUnit = !_settings.value.tempUnitCelsius
        prefs.edit().putBoolean("temp_unit_celsius", newUnit).apply()
        _settings.value = _settings.value.copy(tempUnitCelsius = newUnit)
    }

    fun togglePro(isPro: Boolean) {
        prefs.edit().putBoolean("is_pro", isPro).apply()
        _settings.value = _settings.value.copy(isPro = isPro)
    }

    fun setBillingRegion(region: String) {
        prefs.edit().putString("billing_region", region).apply()
        _settings.value = _settings.value.copy(billingRegion = region)
    }

    fun toggleMemory(enabled: Boolean) {
        prefs.edit().putBoolean("memory_enabled", enabled).apply()
        _settings.value = _settings.value.copy(memoryEnabled = enabled)
    }

    fun updateVoice(voiceId: String, speed: Float, pitch: Float) {
        prefs.edit()
            .putString("voice_id", voiceId)
            .putFloat("voice_speed", speed)
            .putFloat("voice_pitch", pitch)
            .apply()
        _settings.value = _settings.value.copy(voiceId = voiceId, voiceSpeed = speed, voicePitch = pitch)
    }

    fun toggleSpotify() {
        val next = !_settings.value.spotifyConnected
        prefs.edit().putBoolean("spotify_connected", next).apply()
        _settings.value = _settings.value.copy(spotifyConnected = next)
    }

    fun toggleAppleMusic() {
        val next = !_settings.value.appleMusicConnected
        prefs.edit().putBoolean("apple_music_connected", next).apply()
        _settings.value = _settings.value.copy(appleMusicConnected = next)
    }

    fun updateSavedLocations(home: String, work: String) {
        prefs.edit()
            .putString("home_location", home)
            .putString("work_location", work)
            .apply()
        _settings.value = _settings.value.copy(homeLocation = home, workLocation = work)
    }
}
