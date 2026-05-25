package com.mobileaistudio.ui.screens.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mobileaistudio.domain.model.*
import com.mobileaistudio.domain.repository.IChatRepository
import com.mobileaistudio.domain.repository.IModelRepository
import com.mobileaistudio.inference.LlamaCppEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
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

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val allChats: StateFlow<List<ChatConversation>> =
        chatRepository.getAllChats()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private var currentChatId: String = NEW_CHAT_ID
    private var collectJobHolder: Job? = null
    private var generationJob: Job? = null

    companion object {
        const val NEW_CHAT_ID = "new"
    }

    fun loadChat(chatId: String) {
        currentChatId = chatId
        collectJobHolder?.cancel()

        if (chatId == NEW_CHAT_ID) {
            _messages.value = emptyList()
            return
        }
        collectJobHolder = viewModelScope.launch {
            try {
                chatRepository.getMessages(chatId).collect { msgs ->
                    _messages.value = msgs
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load chat"
            }
        }
    }

    fun onInputChanged(text: String) { _inputText.value = text }

    fun clearError() { _error.value = null }

    fun sendMessage() {
        val text = _inputText.value.trim()
        if (text.isBlank() || generationJob?.isActive == true) return

        val capturedText = text
        _isGenerating.value = true
        _error.value = null

        generationJob = viewModelScope.launch {
            try {
                val chatId = ensureChatExists(capturedText)

                val userMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    chatId = chatId,
                    role = MessageRole.USER,
                    content = capturedText
                )
                chatRepository.addMessage(userMsg)

                _inputText.value = ""

                val startTime = System.currentTimeMillis()

                val assistantMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    chatId = chatId,
                    role = MessageRole.ASSISTANT,
                    content = "",
                    isStreaming = true
                )

                val responseText = generateResponse(chatId)
                val elapsed = System.currentTimeMillis() - startTime

                val tokenCount = try {
                    LlamaCppEngine.getTokenCount(responseText)
                } catch (_: Throwable) { responseText.length }

                val finalMsg = assistantMsg.copy(
                    content = responseText,
                    isStreaming = false,
                    generationTimeMs = elapsed,
                    tokensPerSecond = if (elapsed > 0) tokenCount * 1f / (elapsed / 1000f) else 0f
                )
                chatRepository.addMessage(finalMsg)
                _tokensPerSec.value = finalMsg.tokensPerSecond
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                _error.value = e.message ?: "Ошибка генерации"
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun stopGeneration() {
        generationJob?.cancel()
    }

    private suspend fun ensureChatExists(title: String): String {
        if (currentChatId != NEW_CHAT_ID) return currentChatId
        val chat = chatRepository.createChat(
            title = title.take(50),
            modelId = _currentModel.value?.id
        )
        currentChatId = chat.id
        collectJobHolder = viewModelScope.launch {
            chatRepository.getMessages(chat.id).collect { msgs ->
                _messages.value = msgs
            }
        }
        return chat.id
    }

    private suspend fun generateResponse(chatId: String): String {
        val loadedModel = _currentModel.value
        if (loadedModel != null && loadedModel.filePath.isNotEmpty()) {
            if (LlamaCppEngine.isModelLoaded()) {
                val contextMessages = chatRepository.getMessagesForContext(chatId, 20)
                val template = loadedModel.promptTemplate.ifBlank { "%r: %c\n" }
                val contextBuilder = StringBuilder()
                for (msg in contextMessages) {
                    val formatted = template
                        .replace("%r", msg.role.name)
                        .replace("%c", msg.content)
                    contextBuilder.append(formatted)
                }
                contextBuilder.append("ASSISTANT: ")

                val result = withContext(Dispatchers.IO) {
                    LlamaCppEngine.complete(
                        prompt = contextBuilder.toString(),
                        maxTokens = 2048
                    )
                }
                if (!result.startsWith("Error:")) return result
                throw RuntimeException(result.removePrefix("Error: "))
            }
        }
        return if (loadedModel != null) {
            "Модель \"${loadedModel.displayName}\" загружена (stub режим).\n\n" +
            "Для полноценной инференса нужен скомпилированный llama.cpp.\n\n" +
            "Поддерживаемые модели: GGUF формат"
        } else {
            "Нет загруженной модели.\n\n" +
            "Для начала:\n" +
            "1. Перейдите в раздел \"Обзор\"\n" +
            "2. Найдите GGUF модель\n" +
            "3. Скачайте и загрузите её\n" +
            "4. Начните чат"
        }
    }

    fun copyMessage(text: String) {
        val clipboard = application.getSystemService(android.content.Context.CLIPBOARD_SERVICE)
            as android.content.ClipboardManager
        clipboard.setPrimaryClip(android.content.ClipData.newPlainText("message", text))
    }

    fun createNewChat() {
        currentChatId = NEW_CHAT_ID
        _messages.value = emptyList()
        collectJobHolder?.cancel()
    }

    fun deleteChat(chatId: String) {
        viewModelScope.launch {
            chatRepository.deleteChat(chatId)
            if (currentChatId == chatId) {
                currentChatId = NEW_CHAT_ID
                _messages.value = emptyList()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        generationJob?.cancel()
        collectJobHolder?.cancel()
    }

    init {
        viewModelScope.launch {
            modelRepository.getLoadedModel().collect { model ->
                _currentModel.value = model
            }
        }
    }
}
