package com.mobileaistudio.domain.model

data class ChatMessage(
    val id: String,
    val chatId: String,
    val role: MessageRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val thinkingContent: String = "",
    val tokenCount: Int = 0,
    val generationTimeMs: Long = 0,
    val tokensPerSecond: Float = 0f,
    val isRegeneration: Boolean = false,
    val regenGroup: Int = 0,
    val attachments: List<String> = emptyList(),
    val isStreaming: Boolean = false
)

enum class MessageRole { USER, ASSISTANT, SYSTEM }

data class SendResult(
    val success: Boolean = false,
    val errorMessage: String? = null,
    val tokensPerSecond: Float = 0f,
    val totalTokens: Int = 0,
    val generationTimeMs: Long = 0
)
