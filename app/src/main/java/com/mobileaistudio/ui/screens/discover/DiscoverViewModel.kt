package com.mobileaistudio.ui.screens.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileaistudio.data.remote.huggingface.dto.ModelSearchDto
import com.mobileaistudio.domain.repository.IModelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val modelRepository: IModelRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _models = MutableStateFlow<List<ModelSearchDto>>(emptyList())
    val models: StateFlow<List<ModelSearchDto>> = _models

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _selectedCategory = MutableStateFlow("Все")
    val selectedCategory: StateFlow<String> = _selectedCategory

    init { search() }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun search() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val filter = when (_selectedCategory.value) {
                    "Чат-боты" -> "text-generation"
                    "Код" -> "text-generation"
                    "Перевод" -> "translation"
                    "Рассуждения" -> "text-generation"
                    "Суммаризация" -> "summarization"
                    "Изображения" -> "text-to-image"
                    else -> null
                }
                val results = modelRepository.searchHuggingFace(
                    query = _searchQuery.value.ifBlank { "llama gguf" },
                    filter = filter,
                    sort = "downloads",
                    limit = 30
                )
                _models.value = results.filter {
                    it.tags.any { t -> t.contains("gguf", ignoreCase = true) } ||
                    it.libraryName?.equals("gguf", ignoreCase = true) == true ||
                    it.tags.any { t -> t.contains("gguf", ignoreCase = true) }
                }.takeIf { it.isNotEmpty() } ?: results
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка загрузки"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        search()
    }
}
