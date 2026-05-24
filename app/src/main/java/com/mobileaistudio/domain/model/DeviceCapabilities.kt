package com.mobileaistudio.domain.model

data class DeviceCapabilities(
    val cpuModel: String = "Unknown",
    val cpuCores: Int = 4,
    val cpuArchitecture: String = "arm64-v8a",
    val cpuMaxFreqMHz: Long = 2000,
    val neonSupported: Boolean = true,
    val totalRamBytes: Long = 4L * 1024 * 1024 * 1024,
    val availableRamBytes: Long = 2L * 1024 * 1024 * 1024,
    val gpuModel: String = "Unknown",
    val gpuVendor: String = "Unknown",
    val openGLVersion: String = "3.2",
    val vulkanVersion: String? = null,
    val vulkanComputeSupported: Boolean = false,
    val estimatedVRAMBytes: Long = 512L * 1024 * 1024,
    val totalStorageBytes: Long = 64L * 1024 * 1024 * 1024,
    val freeStorageBytes: Long = 32L * 1024 * 1024 * 1024,
    val nnapiAvailable: Boolean = false,
    val nnapiSupportedOps: List<String> = emptyList(),
    val deviceName: String = "Device",
    val deviceManufacturer: String = "Manufacturer",
    val androidVersion: String = "14",
    val sdkVersion: Int = 26
) {
    val totalRamGB: Float get() = totalRamBytes / (1024f * 1024f * 1024f)
    val availableRamGB: Float get() = availableRamBytes / (1024f * 1024f * 1024f)
    val totalStorageGB: Float get() = totalStorageBytes / (1024f * 1024f * 1024f)
    val freeStorageGB: Float get() = freeStorageBytes / (1024f * 1024f * 1024f)
    val estimatedVRAMGB: Float get() = estimatedVRAMBytes / (1024f * 1024f * 1024f)
}
