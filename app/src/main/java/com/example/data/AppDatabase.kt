package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        TaskEntity::class,
        ReminderEntity::class,
        MemoryEntity::class,
        ConversationEntity::class,
        ChatMessageEntity::class,
        HealthMetricEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun reminderDao(): ReminderDao
    abstract fun memoryDao(): MemoryDao
    abstract fun conversationDao(): ConversationDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun healthMetricDao(): HealthMetricDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mind_assistant_database"
                )
                .addCallback(DatabaseCallback(context))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    val database = getDatabase(context)
                    populateInitialData(database)
                }
            }

            private suspend fun populateInitialData(db: AppDatabase) {
                // Initial Tasks
                db.taskDao().insertTask(
                    TaskEntity(
                        title = "Finish interior design moodboard",
                        description = "Winter palette concept for studio client",
                        isCompleted = false,
                        priority = "High",
                        deadline = "5:00 PM Today",
                        category = "Project",
                        orderIndex = 0
                    )
                )
                db.taskDao().insertTask(
                    TaskEntity(
                        title = "Review typography & sample materials",
                        description = "Check foam board samples",
                        isCompleted = false,
                        priority = "Medium",
                        deadline = "Tomorrow",
                        category = "Study",
                        orderIndex = 1
                    )
                )
                db.taskDao().insertTask(
                    TaskEntity(
                        title = "Submit project outline draft",
                        description = "Architecture & interior proposal",
                        isCompleted = true,
                        priority = "High",
                        deadline = "Yesterday",
                        category = "Study",
                        orderIndex = 2
                    )
                )
                db.taskDao().insertTask(
                    TaskEntity(
                        title = "Schedule sync with team",
                        description = "Discuss rendering deliverables",
                        isCompleted = true,
                        priority = "Low",
                        deadline = "Yesterday",
                        category = "Work",
                        orderIndex = 3
                    )
                )
                db.taskDao().insertTask(
                    TaskEntity(
                        title = "Order foam board for prototype",
                        description = "Needed for physical layout model",
                        isCompleted = false,
                        priority = "Medium",
                        deadline = "Friday",
                        category = "Shopping",
                        orderIndex = 4
                    )
                )

                // Initial Reminders
                db.reminderDao().insertReminder(
                    ReminderEntity(
                        title = "Buy foam board for model",
                        timeFormatted = "4:30 PM",
                        dateFormatted = "Today",
                        recurringPattern = "None",
                        locationName = "Art Supply Store"
                    )
                )
                db.reminderDao().insertReminder(
                    ReminderEntity(
                        title = "Project review meeting with team",
                        timeFormatted = "4:00 PM",
                        dateFormatted = "Today",
                        recurringPattern = "None",
                        locationName = "University Studio"
                    )
                )
                db.reminderDao().insertReminder(
                    ReminderEntity(
                        title = "Drink water & take 5-min eye break",
                        timeFormatted = "Every 2 hours",
                        dateFormatted = "Recurring",
                        recurringPattern = "Daily"
                    )
                )

                // Initial Memories
                db.memoryDao().insertMemory(
                    MemoryEntity(
                        content = "Your interior design project uses a winter color palette with icy blues, warm neutrals, and brushed brass.",
                        category = "Project",
                        isPinned = true
                    )
                )
                db.memoryDao().insertMemory(
                    MemoryEntity(
                        content = "Favorite drink is iced Spanish latte with oat milk.",
                        category = "Preference",
                        isPinned = false
                    )
                )
                db.memoryDao().insertMemory(
                    MemoryEntity(
                        content = "Final studio presentation deadline is September 14 at 10:00 AM.",
                        category = "Goal",
                        isPinned = true
                    )
                )

                // Initial Conversation & Messages
                val convId = "conv_default_1"
                db.conversationDao().insertConversation(
                    ConversationEntity(
                        id = convId,
                        title = "Planning interior design project",
                        updatedAt = System.currentTimeMillis()
                    )
                )
                db.chatMessageDao().insertMessage(
                    ChatMessageEntity(
                        conversationId = convId,
                        sender = "user",
                        text = "Hi MIND, can you help me structure the design presentation for Friday?",
                        timestamp = System.currentTimeMillis() - 60000
                    )
                )
                db.chatMessageDao().insertMessage(
                    ChatMessageEntity(
                        conversationId = convId,
                        sender = "assistant",
                        text = "Good morning, Raneem! ✨ I'd love to help. Based on your project notes, here is a recommended 4-slide structure:\n\n1. **Concept & Mood**: Highlight the winter palette (icy blue, deep slate, warm neutrals).\n2. **Spatial Layout**: Showcase the 3D renders and zone circulation.\n3. **Material Board**: Brushed brass accents and matte ceramics.\n4. **Deliverables & Timeline**: Prototype handover due September 14.\n\nShall we draft the narrative for the first slide?",
                        timestamp = System.currentTimeMillis() - 30000
                    )
                )

                // Initial Health Metric
                db.healthMetricDao().insertOrUpdate(
                    HealthMetricEntity(
                        date = "today",
                        steps = 6482,
                        stepGoal = 10000,
                        distanceKm = 4.8f,
                        caloriesBurned = 420,
                        waterMl = 1750,
                        waterGoalMl = 2500,
                        sleepHours = 7.5f,
                        activeMinutes = 45
                    )
                )
            }
        }
    }
}
