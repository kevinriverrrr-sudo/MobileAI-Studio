package com.mobileaistudio.ui.screens.mymodels

import android.os.Environment
import android.os.StatFs
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileaistudio.domain.model.AIModel
import com.mobileaistudio.domain.repository.IModelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
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

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        viewModelScope.launch {
            models.collect { mList ->
                val totalBytes = mList.sumOf { it.fileSizeBytes }
                val stat = try { StatFs(Environment.getDataDirectory().path) } catch (_: Exception) { null }
                val totalStorage = stat?.totalBytes ?: 64L * 1024 * 1024 * 1024
                _storageUsed.value = if (totalStorage > 0) {
                    (totalBytes.toFloat() / totalStorage.toFloat()).coerceIn(0f, 1f)
                } else 0f
            }
        }
    }

    fun clearError() { _error.value = null }

    fun toggleModelLoaded(modelId: String) {
        viewModelScope.launch {
            try {
                val model = modelRepository.getModelById(modelId) ?: return@launch
                val newLoaded = !model.isLoaded
                modelRepository.setModelLoaded(modelId, newLoaded)
                if (newLoaded) modelRepository.updateLastUsed(modelId)
            } catch (e: CancellationException) { throw e } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка переключения модели"
                Log.e("MyModelsVM", "toggleModelLoaded failed", e)
            }
        }
    }

    fun deleteModel(modelId: String) {
        viewModelScope.launch {
            try {
                modelRepository.deleteModel(modelId)
            } catch (e: CancellationException) { throw e } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка удаления модели"
                Log.e("MyModelsVM", "deleteModel failed", e)
            }
        }
    }
}
