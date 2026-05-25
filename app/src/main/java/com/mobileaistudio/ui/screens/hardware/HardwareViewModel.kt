package com.mobileaistudio.ui.screens.hardware

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileaistudio.domain.model.DeviceCapabilities
import com.mobileaistudio.domain.repository.IHardwareRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HardwareViewModel @Inject constructor(
    private val hardwareRepository: IHardwareRepository
) : ViewModel() {

    private val _device = MutableStateFlow<DeviceCapabilities?>(null)
    val deviceCapabilities: StateFlow<DeviceCapabilities?> = _device

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun detect() {
        viewModelScope.launch {
            try {
                _device.value = withContext(Dispatchers.IO) {
                    hardwareRepository.detectCapabilities()
                }
                _error.value = null
            } catch (e: CancellationException) { throw e } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка определения характеристик"
            }
        }
    }
}
