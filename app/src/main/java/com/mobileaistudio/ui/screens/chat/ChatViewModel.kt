package com.mobileaistudio.ui.screens.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mobileaistudio.domain.model.*
import com.mobileaistudio.domain.repository.IChatRepository
import com.mobileaistudio.domain.repository.IModelRepository
import com.mobileaistudio.inference.LlamaCppEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
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
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var currentChatId: String = "new"
    private var collectJobHolder: Job? = null

    fun loadChat(chatId: String) {
        currentChatId = chatId
        // Cancel previous collection
        collectJobHolder?.cancel()

        if (chatId == "new") {
            _messages.value = emptyList()
            return
        }
        collectJobHolder = viewModelScope.launch {
            chatRepository.getMessages(chatId).collect { msgs ->
                _messages.value = msgs
            }
        }
    }

    fun onInputChanged(text: String) { _inputText.value = text }

    fun sendMessage() {
        val text = _inputText.value.trim()
        if (text.isBlank() || _isGenerating.value) return

        // Capture the text BEFORE any coroutine launch to avoid race condition
        val capturedText = text

        viewModelScope.launch {
            val chatId = ensureChatExists(capturedText)

            val userMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                chatId = chatId,
                role = MessageRole.USER,
                content = capturedText
            )
            chatRepository.addMessage(userMsg)

            // Clear input immediately on main thread
            _inputText.value = ""
            _isGenerating.value = true
            _error.value = null

            val startTime = System.currentTimeMillis()

            val assistantMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                chatId = chatId,
                role = MessageRole.ASSISTANT,
                content = "",
                isStreaming = true
            )

            try {
                val responseText = generateResponse(capturedText, chatId)
                val elapsed = System.currentTimeMillis() - startTime

                val finalMsg = assistantMsg.copy(
                    content = responseText,
                    isStreaming = false,
                    generationTimeMs = elapsed,
                    tokensPerSecond = if (elapsed > 0) responseText.length * 1f / (elapsed / 1000f) else 0f
                )
                chatRepository.addMessage(finalMsg)
                _tokensPerSec.value = finalMsg.tokensPerSecond
            } catch (e: Exception) {
                val errorMsg = assistantMsg.copy(
                    content = "Ошибка генерации: ${e.message}",
                    isStreaming = false
                )
                chatRepository.addMessage(errorMsg)
                _error.value = e.message
            } finally {
                _isGenerating.value = false
            }
        }
    }

    private suspend fun ensureChatExists(title: String): String {
        if (currentChatId != "new") return currentChatId
        val chat = chatRepository.createChat(
            title = title.take(50),
            modelId = _currentModel.value?.id
        )
        currentChatId = chat.id
        // Start collecting messages for the new chat
        collectJobHolder = viewModelScope.launch {
            chatRepository.getMessages(chat.id).collect { msgs ->
                _messages.value = msgs
            }
        }
        return chat.id
    }

    private suspend fun generateResponse(prompt: String, chatId: String): String {
        val loadedModel = _currentModel.value
        if (loadedModel != null && loadedModel.filePath.isNotEmpty()) {
            // Try local inference via LlamaCppEngine
            if (LlamaCppEngine.isModelLoaded()) {
                val contextMessages = chatRepository.getMessagesForContext(chatId, 20)
                val contextBuilder = StringBuilder()
                for (msg in contextMessages) {
                    contextBuilder.append("${msg.role.name}: ${msg.content}\n")
                }
                contextBuilder.append("ASSISTANT: ")
                val result = LlamaCppEngine.complete(
                    prompt = contextBuilder.toString(),
                    maxTokens = 2048
                )
                if (!result.startsWith("Error:")) return result
            }
        }
        // Fallback: check if a model is loaded in DB (stub mode response)
        return if (loadedModel != null) {
            "Модель \"${loadedModel.displayName}\" загружена (stub режим).\n\n" +
            "Для полноценной инференса нужен скомпилированный llama.cpp.\n\n" +
            "Ваш запрос: \"$prompt\""
        } else {
            "Нет загруженной модели.\n\n" +
            "Для начала:\n" +
            "1. Перейдите в раздел \"Обзор\"\n" +
            "2. Найдите GGUF модель\n" +
            "3. Скачайте и загрузите её\n" +
            "4. Начните чат"
        }
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
        collectJobHolder?.cancel()
    }

    fun deleteChat(chatId: String) {
        viewModelScope.launch {
            chatRepository.deleteChat(chatId)
            if (currentChatId == chatId) {
                currentChatId = "new"
                _messages.value = emptyList()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        collectJobHolder?.cancel()
    }

    init {
        // Observe the currently loaded model
        viewModelScope.launch {
            modelRepository.getLoadedModel().collect { model ->
                _currentModel.value = model
            }
        }
    }
}
