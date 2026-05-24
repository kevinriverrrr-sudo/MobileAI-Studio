package com.mobileaistudio.data.local.db.entities

import androidx.room.*

@Entity(
    tableName = "messages",
    foreignKeys = [ForeignKey(
        entity = ChatEntity::class,
        parentColumns = ["id"],
        childColumns = ["chatId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("chatId")]
)
data class MessageEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val role: String, // "user", "assistant", "system"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val thinkingContent: String = "",
    val tokenCount: Int = 0,
    val generationTimeMs: Long = 0,
    val tokensPerSecond: Float = 0f,
    val isRegeneration: Boolean = false,
    val regenGroup: Int = 0,
    val attachments: String = "" // JSON list of file paths
)
