package com.mobileaistudio.domain.repository

import com.mobileaistudio.domain.model.DeviceCapabilities
import kotlinx.coroutines.flow.Flow

interface IHardwareRepository {
    suspend fun detectCapabilities(): DeviceCapabilities
    fun getCapabilities(): Flow<DeviceCapabilities>
}
