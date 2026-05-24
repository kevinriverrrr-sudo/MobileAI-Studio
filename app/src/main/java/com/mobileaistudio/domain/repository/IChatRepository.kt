package com.mobileaistudio.domain.repository

import com.mobileaistudio.domain.model.ChatConversation
import com.mobileaistudio.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface IChatRepository {
    fun getAllChats(): Flow<List<ChatConversation>>
    suspend fun getChat(id: String): ChatConversation?
    fun getMessages(chatId: String): Flow<List<ChatMessage>>
    suspend fun createChat(title: String, modelId: String?): ChatConversation
    suspend fun updateChatTitle(chatId: String, title: String)
    suspend fun deleteChat(chatId: String)
    suspend fun addMessage(message: ChatMessage)
    suspend fun updateMessage(message: ChatMessage)
    suspend fun deleteMessage(messageId: String)
    suspend fun getMessagesForContext(chatId: String, limit: Int): List<ChatMessage>
    suspend fun pinChat(chatId: String, pin: Boolean)
    suspend fun archiveChat(chatId: String)
}
