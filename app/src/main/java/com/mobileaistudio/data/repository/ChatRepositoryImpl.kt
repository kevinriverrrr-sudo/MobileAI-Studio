package com.mobileaistudio.data.repository

import com.mobileaistudio.data.local.db.dao.ChatDao
import com.mobileaistudio.data.local.db.entities.ChatEntity
import com.mobileaistudio.data.local.db.entities.MessageEntity
import com.mobileaistudio.domain.model.ChatConversation
import com.mobileaistudio.domain.model.ChatMessage
import com.mobileaistudio.domain.model.MessageRole
import com.mobileaistudio.domain.repository.IChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(private val chatDao: ChatDao) : IChatRepository {

    override fun getAllChats(): Flow<List<ChatConversation>> =
        chatDao.getAllChats().map { list -> list.map { it.toDomain() } }

    override suspend fun getChat(id: String): ChatConversation? =
        chatDao.getChatById(id)?.toDomain()

    override fun getMessages(chatId: String): Flow<List<ChatMessage>> =
        chatDao.getMessages(chatId).map { list -> list.map { it.toDomain() } }

    override suspend fun createChat(title: String, modelId: String?): ChatConversation {
        val id = UUID.randomUUID().toString()
        val entity = ChatEntity(
            id = id, title = title.ifBlank { "Новый чат" }, modelId = modelId,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        chatDao.insertChat(entity)
        return entity.toDomain()
    }

    override suspend fun updateChatTitle(chatId: String, title: String) {
        val chat = chatDao.getChatById(chatId) ?: return
        chatDao.updateChat(chat.copy(title = title, updatedAt = System.currentTimeMillis()))
    }

    override suspend fun deleteChat(chatId: String) {
        chatDao.deleteMessages(chatId)
        chatDao.deleteChatById(chatId)
    }

    override suspend fun addMessage(message: ChatMessage) {
        chatDao.insertMessage(message.toEntity())
        chatDao.updateChatTimestamp(message.chatId, System.currentTimeMillis())
    }

    override suspend fun updateMessage(message: ChatMessage) {
        chatDao.updateMessage(message.toEntity())
    }

    override suspend fun deleteMessage(messageId: String) {
        chatDao.deleteMessage(messageId)
    }

    override suspend fun getMessagesForContext(chatId: String, limit: Int): List<ChatMessage> {
        // Use kotlinx.coroutines.flow.first() instead of custom extension
        return chatDao.getMessages(chatId).first()
            .takeLast(limit)
            .map { it.toDomain() }
    }

    override suspend fun pinChat(chatId: String, pin: Boolean) {
        val chat = chatDao.getChatById(chatId) ?: return
        chatDao.updateChat(chat.copy(isPinned = pin))
    }

    override suspend fun archiveChat(chatId: String) {
        val chat = chatDao.getChatById(chatId) ?: return
        chatDao.updateChat(chat.copy(isArchived = true))
    }

    private fun ChatEntity.toDomain() = ChatConversation(
        id = id, title = title.ifBlank { "Новый чат" }, modelId = modelId, modelName = "",
        createdAt = createdAt, updatedAt = updatedAt,
        folderId = folderId, isPinned = isPinned, isArchived = isArchived,
        messageCount = 0,
        lastMessagePreview = ""
    )

    private fun MessageEntity.toDomain() = ChatMessage(
        id = id, chatId = chatId,
        role = parseRole(role),
        content = content, timestamp = timestamp,
        thinkingContent = thinkingContent, tokenCount = tokenCount,
        generationTimeMs = generationTimeMs, tokensPerSecond = tokensPerSecond,
        isRegeneration = isRegeneration, regenGroup = regenGroup
    )

    private fun ChatMessage.toEntity() = MessageEntity(
        id = id, chatId = chatId,
        role = role.name, content = content, timestamp = timestamp,
        thinkingContent = thinkingContent, tokenCount = tokenCount,
        generationTimeMs = generationTimeMs, tokensPerSecond = tokensPerSecond,
        isRegeneration = isRegeneration, regenGroup = regenGroup,
        attachments = attachments.joinToString(",")
    )

    private fun parseRole(roleStr: String): MessageRole {
        return try {
            MessageRole.valueOf(roleStr.uppercase())
        } catch (e: IllegalArgumentException) {
            // Fallback for invalid role strings (e.g., "user" instead of "USER")
            when (roleStr.lowercase()) {
                "user" -> MessageRole.USER
                "assistant" -> MessageRole.ASSISTANT
                "system" -> MessageRole.SYSTEM
                else -> MessageRole.USER
            }
        }
    }
}
