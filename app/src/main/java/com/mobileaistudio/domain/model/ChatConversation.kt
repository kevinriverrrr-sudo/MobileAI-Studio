package com.mobileaistudio.domain.model

data class ChatConversation(
    val id: String,
    val title: String = "Новый чат",
    val modelId: String? = null,
    val modelName: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val folderId: String? = null,
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val messageCount: Int = 0,
    val lastMessagePreview: String = ""
)
