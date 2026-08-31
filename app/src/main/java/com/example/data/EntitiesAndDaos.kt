package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val priority: String = "Medium", // Low, Medium, High
    val deadline: String = "Today",
    val category: String = "Personal",
    val orderIndex: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, orderIndex ASC, id DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY orderIndex ASC")
    fun getPendingTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("UPDATE tasks SET isCompleted = :completed WHERE id = :id")
    suspend fun setTaskCompleted(id: Long, completed: Boolean)
}

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val timeFormatted: String = "5:00 PM",
    val dateFormatted: String = "Today",
    val recurringPattern: String = "None", // None, Daily, Weekly
    val locationName: String = "",
    val isCompleted: Boolean = false,
    val isLocationBased: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY isCompleted ASC, id DESC")
    fun getAllReminders(): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("UPDATE reminders SET isCompleted = :completed WHERE id = :id")
    suspend fun setReminderCompleted(id: Long, completed: Boolean)
}

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val category: String = "Preference", // Preference, Project, Personal, Goal
    val dateAdded: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
)

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories ORDER BY isPinned DESC, id DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memories WHERE content LIKE '%' || :query || '%'")
    fun searchMemories(query: String): Flow<List<MemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity): Long

    @Update
    suspend fun updateMemory(memory: MemoryEntity)

    @Delete
    suspend fun deleteMemory(memory: MemoryEntity)

    @Query("DELETE FROM memories")
    suspend fun deleteAllMemories()
}

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isTemporary: Boolean = false,
    val modelName: String = "Gemini Pro",
    val personality: String = "Friendly & Helpful"
)

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations WHERE isTemporary = 0 ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :id")
    suspend fun getConversationById(id: String): ConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity)

    @Update
    suspend fun updateConversation(conversation: ConversationEntity)

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversation(id: String)
}

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: String,
    val sender: String, // "user" or "assistant"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val toolType: String? = null,
    val sources: String? = null, // JSON / delimited URLs
    val imageAttachmentUri: String? = null
)

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE conversationId = :convId ORDER BY timestamp ASC")
    fun getMessagesForConversation(convId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages WHERE conversationId = :convId")
    suspend fun deleteMessagesForConversation(convId: String)
}

@Entity(tableName = "health_metrics")
data class HealthMetricEntity(
    @PrimaryKey val date: String, // YYYY-MM-DD
    val steps: Int = 6482,
    val stepGoal: Int = 10000,
    val distanceKm: Float = 4.8f,
    val caloriesBurned: Int = 420,
    val waterMl: Int = 1750,
    val waterGoalMl: Int = 2500,
    val sleepHours: Float = 7.5f,
    val activeMinutes: Int = 45
)

@Dao
interface HealthMetricDao {
    @Query("SELECT * FROM health_metrics WHERE date = :date")
    fun getMetricForDate(date: String): Flow<HealthMetricEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(metric: HealthMetricEntity)
}
