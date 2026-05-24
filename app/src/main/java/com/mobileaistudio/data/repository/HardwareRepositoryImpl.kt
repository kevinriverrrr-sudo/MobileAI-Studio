package com.mobileaistudio.data.repository

import android.app.ActivityManager
import android.content.Context
import android.opengl.GLES20
import android.os.Build
import com.mobileaistudio.domain.model.DeviceCapabilities
import com.mobileaistudio.domain.repository.IHardwareRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HardwareRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : IHardwareRepository {

    private val _capabilities = MutableStateFlow(DeviceCapabilities())
    override fun getCapabilities(): StateFlow<DeviceCapabilities> = _capabilities

    override suspend fun detectCapabilities(): DeviceCapabilities {
        val caps = DeviceCapabilities(
            cpuModel = detectCPU(),
            cpuCores = Runtime.getRuntime().availableProcessors(),
            cpuArchitecture = Build.SUPPORTED_ABIS.firstOrNull() ?: "unknown",
            cpuMaxFreqMHz = getMaxCPUFreq(),
            neonSupported = Build.SUPPORTED_ABIS.any { it.contains("arm64") || it.contains("armeabi-v7a") },
            totalRamBytes = getTotalRAM(),
            availableRamBytes = getAvailableRAM(),
            gpuModel = detectGPU(),
            gpuVendor = detectGPUVendor(),
            openGLVersion = detectOpenGLVersion(),
            vulkanVersion = detectVulkanVersion(),
            vulkanComputeSupported = detectVulkanVersion() != null,
            estimatedVRAMBytes = estimateVRAM(),
            totalStorageBytes = getTotalStorage(),
            freeStorageBytes = getFreeStorage(),
            nnapiAvailable = isNNAPIAvailable(),
            nnapiSupportedOps = emptyList(),
            deviceName = "${Build.MANUFACTURER} ${Build.MODEL}",
            deviceManufacturer = Build.MANUFACTURER,
            androidVersion = Build.VERSION.RELEASE,
            sdkVersion = Build.VERSION.SDK_INT
        )
        _capabilities.value = caps
        return caps
    }

    private fun detectCPU(): String {
        return try {
            val cpuInfo = File("/proc/cpuinfo").readText()
            val hardwareLine = cpuInfo.lines().find { it.startsWith("Hardware") }
            hardwareLine?.substringAfter(":")?.trim() ?: Build.HARDWARE
        } catch (_: Exception) {
            Build.HARDWARE
        }
    }

    private fun getMaxCPUFreq(): Long {
        return try {
            val cpu0 = File("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq")
            if (cpu0.exists()) cpu0.readText().trim().toLong() / 1000
            else 2000L
        } catch (_: Exception) {
            2000L
        }
    }

    private fun getTotalRAM(): Long {
        return try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val info = ActivityManager.MemoryInfo()
            am.getMemoryInfo(info)
            info.totalMem
        } catch (_: Exception) {
            4L * 1024 * 1024 * 1024
        }
    }

    private fun getAvailableRAM(): Long {
        return try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val info = ActivityManager.MemoryInfo()
            am.getMemoryInfo(info)
            info.availMem
        } catch (_: Exception) {
            2L * 1024 * 1024 * 1024
        }
    }

    private fun detectGPU(): String {
        return try {
            val glRenderer = GLES20.glGetString(GLES20.GL_RENDERER) ?: ""
            if (glRenderer.isNotEmpty()) glRenderer else Build.HARDWARE
        } catch (_: Exception) {
            Build.HARDWARE
        }
    }

    private fun detectGPUVendor(): String {
        return try {
            GLES20.glGetString(GLES20.GL_VENDOR) ?: "Unknown"
        } catch (_: Exception) {
            "Unknown"
        }
    }

    private fun detectOpenGLVersion(): String {
        return try {
            val glVersion = GLES20.glGetString(GLES20.GL_VERSION) ?: "3.2"
            // Extract version number, e.g., "OpenGL ES 3.2 v@...."
            val match = Regex("""OpenGL ES (\d+\.\d+)""").find(glVersion)
            match?.groupValues?.get(1) ?: "3.2"
        } catch (_: Exception) {
            "3.2"
        }
    }

    private fun detectVulkanVersion(): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                // Check if vulkan is available
                val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val deviceConfig = activityManager.deviceConfigurationInfo
                if (deviceConfig.reqGlEsVersion >= 0x30000) "1.1+" else "1.0+"
            } catch (_: Exception) {
                null
            }
        } else null
    }

    private fun estimateVRAM(): Long {
        // Estimate VRAM as ~25% of total RAM for mobile GPUs
        val totalRAM = getTotalRAM()
        return (totalRAM * 0.25).toLong().coerceIn(256L * 1024 * 1024, 4L * 1024 * 1024 * 1024)
    }

    private fun getTotalStorage(): Long {
        return try {
            val stat = android.os.StatFs(android.os.Environment.getDataDirectory().path)
            stat.totalBytes
        } catch (_: Exception) {
            64L * 1024 * 1024 * 1024
        }
    }

    private fun getFreeStorage(): Long {
        return try {
            val stat = android.os.StatFs(android.os.Environment.getDataDirectory().path)
            stat.availableBytes
        } catch (_: Exception) {
            32L * 1024 * 1024 * 1024
        }
    }

    private fun isNNAPIAvailable(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    }
}
