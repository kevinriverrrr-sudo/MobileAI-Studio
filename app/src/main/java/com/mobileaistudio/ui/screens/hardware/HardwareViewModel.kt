package com.mobileaistudio.ui.screens.hardware

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileaistudio.domain.model.DeviceCapabilities
import com.mobileaistudio.domain.repository.IHardwareRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HardwareViewModel @Inject constructor(
    private val hardwareRepository: IHardwareRepository
) : ViewModel() {

    private val _device = MutableStateFlow<DeviceCapabilities?>(null)
    val deviceCapabilities: StateFlow<DeviceCapabilities?> = _device

    fun detect() {
        viewModelScope.launch {
            _device.value = hardwareRepository.detectCapabilities()
        }
    }
}
