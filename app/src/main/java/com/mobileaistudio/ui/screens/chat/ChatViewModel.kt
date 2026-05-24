package com.mobileaistudio.ui.screens.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mobileaistudio.data.local.db.entities.MessageEntity
import com.mobileaistudio.data.local.db.entities.ChatEntity
import com.mobileaistudio.domain.model.*
import com.mobileaistudio.domain.repository.IChatRepository
import com.mobileaistudio.domain.repository.IModelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: IChatRepository,
    private val modelRepository: IModelRepository,
    private val application: Application
) : AndroidViewModel(application) {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating

    private val _tokensPerSec = MutableStateFlow(0f)
    val tokensPerSec: StateFlow<Float> = _tokensPerSec

    private val _currentModel = MutableStateFlow<AIModel?>(null)
    val currentModel: StateFlow<AIModel?> = _currentModel

    private var currentChatId: String = "new"

    fun loadChat(chatId: String) {
        currentChatId = chatId
        if (chatId == "new") {
            _messages.value = emptyList()
            return
        }
        viewModelScope.launch {
            chatRepository.getMessages(chatId).collect { msgs ->
                _messages.value = msgs
            }
        }
        viewModelScope.launch {
            modelRepository.getLoadedModel().collect { model ->
                _currentModel.value = model
            }
        }
    }

    fun onInputChanged(text: String) { _inputText.value = text }

    fun sendMessage() {
        val text = _inputText.value.trim()
        if (text.isBlank() || _isGenerating.value) return

        viewModelScope.launch {
            val chatId = ensureChatExists()

            val userMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                chatId = chatId,
                role = MessageRole.USER,
                content = text
            )
            chatRepository.addMessage(userMsg)

            _isGenerating.value = true
            val startTime = System.currentTimeMillis()

            val assistantMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                chatId = chatId,
                role = MessageRole.ASSISTANT,
                content = "",
                isStreaming = true
            )

            // Simulate local inference (real JNI call in production)
            val responseText = generateLocalResponse(text)
            val elapsed = System.currentTimeMillis() - startTime

            val finalMsg = assistantMsg.copy(
                content = responseText,
                isStreaming = false,
                generationTimeMs = elapsed,
                tokensPerSecond = if (elapsed > 0) responseText.length * 1f / (elapsed / 1000f) else 0f
            )
            chatRepository.addMessage(finalMsg)
            _isGenerating.value = false
            _tokensPerSec.value = finalMsg.tokensPerSecond
        }

        _inputText.value = ""
    }

    private suspend fun ensureChatExists(): String {
        if (currentChatId != "new") return currentChatId
        val chat = chatRepository.createChat(
            title = _inputText.value.take(50),
            modelId = _currentModel.value?.id
        )
        currentChatId = chat.id
        return chat.id
    }

    private fun generateLocalResponse(prompt: String): String {
        // Placeholder: In production this calls LlamaCppEngine JNI
        return "Это демонстрационный ответ. Подключите GGUF модель для реальной инференса.\n\n" +
               "Вы написали: \"$prompt\"\n\n" +
               "Для работы с моделями:\n" +
               "1. Перейдите в раздел \"Обзор\"\n" +
               "2. Найдите и скачайте GGUF модель\n" +
               "3. Загрузите её в память\n" +
               "4. Начните чат"
    }

    fun stopGeneration() { _isGenerating.value = false }

    fun copyMessage(text: String) {
        val clipboard = application.getSystemService(android.content.Context.CLIPBOARD_SERVICE)
            as android.content.ClipboardManager
        clipboard.setPrimaryClip(android.content.ClipData.newPlainText("message", text))
    }

    fun createNewChat() {
        currentChatId = "new"
        _messages.value = emptyList()
    }
}
