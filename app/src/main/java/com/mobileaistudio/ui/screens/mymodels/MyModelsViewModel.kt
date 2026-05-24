package com.mobileaistudio.ui.screens.mymodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileaistudio.domain.model.AIModel
import com.mobileaistudio.domain.repository.IModelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyModelsViewModel @Inject constructor(
    private val modelRepository: IModelRepository
) : ViewModel() {

    val models: StateFlow<List<AIModel>> = modelRepository.getLocalModels()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val loadedModel: StateFlow<AIModel?> = modelRepository.getLoadedModel()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _storageUsed = MutableStateFlow(0f)
    val storageUsed: StateFlow<Float> = _storageUsed

    init {
        viewModelScope.launch {
            models.collect { mList ->
                val totalBytes = mList.sumOf { it.fileSizeBytes }
                _storageUsed.value = if (totalBytes > 0) 0.3f else 0f // Placeholder
            }
        }
    }

    fun toggleModelLoaded(modelId: String) {
        viewModelScope.launch {
            val model = modelRepository.getModelById(modelId) ?: return@launch
            val newLoaded = !model.isLoaded
            modelRepository.setModelLoaded(modelId, newLoaded)
            if (newLoaded) modelRepository.updateLastUsed(modelId)
        }
    }

    fun deleteModel(modelId: String) {
        viewModelScope.launch {
            modelRepository.deleteModel(modelId)
        }
    }
}
