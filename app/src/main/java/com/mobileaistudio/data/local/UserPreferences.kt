package com.mobileaistudio.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferences(private val context: Context) {

    private object Keys {
        val HF_TOKEN = stringPreferencesKey("hf_token")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val WIFI_ONLY = booleanPreferencesKey("wifi_only")
        val MAX_PARALLEL_DOWNLOADS = intPreferencesKey("max_parallel_downloads")
        val GPU_BACKEND = stringPreferencesKey("gpu_backend")
        val AUTO_GPU_OFFLOAD = booleanPreferencesKey("auto_gpu_offload")
        val THREAD_COUNT = intPreferencesKey("thread_count")
        val FLASH_ATTENTION = booleanPreferencesKey("flash_attention")
        val STREAMING_OUTPUT = booleanPreferencesKey("streaming_output")
        val SHOW_THINKING = booleanPreferencesKey("show_thinking")
        val MAX_HISTORY = intPreferencesKey("max_history")
        val MODELS_DIR = stringPreferencesKey("models_dir")
        val CLOUD_INFERENCE = booleanPreferencesKey("cloud_inference")
        val DEFAULT_PROVIDER = stringPreferencesKey("default_provider")
        val PROVIDER_STRATEGY = stringPreferencesKey("provider_strategy")
        val DEVELOPER_MODE = booleanPreferencesKey("developer_mode")
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
    }

    val hfToken: Flow<String> = context.dataStore.data.map { it[Keys.HF_TOKEN] ?: "" }
    val themeMode: Flow<String> = context.dataStore.data.map { it[Keys.THEME_MODE] ?: "system" }
    val darkTheme: Flow<Boolean> = context.dataStore.data.map { it[Keys.DARK_THEME] ?: true }
    val language: Flow<String> = context.dataStore.data.map { it[Keys.LANGUAGE] ?: "ru" }
    val wifiOnly: Flow<Boolean> = context.dataStore.data.map { it[Keys.WIFI_ONLY] ?: true }
    val maxParallelDownloads: Flow<Int> = context.dataStore.data.map { it[Keys.MAX_PARALLEL_DOWNLOADS] ?: 4 }
    val gpuBackend: Flow<String> = context.dataStore.data.map { it[Keys.GPU_BACKEND] ?: "vulkan" }
    val autoGpuOffload: Flow<Boolean> = context.dataStore.data.map { it[Keys.AUTO_GPU_OFFLOAD] ?: true }
    val threadCount: Flow<Int> = context.dataStore.data.map { it[Keys.THREAD_COUNT] ?: 0 }
    val flashAttention: Flow<Boolean> = context.dataStore.data.map { it[Keys.FLASH_ATTENTION] ?: true }
    val streamingOutput: Flow<Boolean> = context.dataStore.data.map { it[Keys.STREAMING_OUTPUT] ?: true }
    val showThinking: Flow<Boolean> = context.dataStore.data.map { it[Keys.SHOW_THINKING] ?: true }
    val maxHistory: Flow<Int> = context.dataStore.data.map { it[Keys.MAX_HISTORY] ?: 20 }
    val modelsDir: Flow<String> = context.dataStore.data.map { it[Keys.MODELS_DIR] ?: "" }
    val cloudInference: Flow<Boolean> = context.dataStore.data.map { it[Keys.CLOUD_INFERENCE] ?: true }
    val defaultProvider: Flow<String> = context.dataStore.data.map { it[Keys.DEFAULT_PROVIDER] ?: "fastest" }
    val providerStrategy: Flow<String> = context.dataStore.data.map { it[Keys.PROVIDER_STRATEGY] ?: "fastest" }
    val developerMode: Flow<Boolean> = context.dataStore.data.map { it[Keys.DEVELOPER_MODE] ?: false }
    val onboardingDone: Flow<Boolean> = context.dataStore.data.map { it[Keys.ONBOARDING_DONE] ?: false }

    suspend fun setHfToken(token: String) = context.dataStore.edit { it[Keys.HF_TOKEN] = token }
    suspend fun setThemeMode(mode: String) = context.dataStore.edit { it[Keys.THEME_MODE] = mode }
    suspend fun setLanguage(lang: String) = context.dataStore.edit { it[Keys.LANGUAGE] = lang }
    suspend fun setWifiOnly(v: Boolean) = context.dataStore.edit { it[Keys.WIFI_ONLY] = v }
    suspend fun setMaxParallelDownloads(n: Int) = context.dataStore.edit { it[Keys.MAX_PARALLEL_DOWNLOADS] = n }
    suspend fun setGpuBackend(b: String) = context.dataStore.edit { it[Keys.GPU_BACKEND] = b }
    suspend fun setAutoGpuOffload(v: Boolean) = context.dataStore.edit { it[Keys.AUTO_GPU_OFFLOAD] = v }
    suspend fun setThreadCount(n: Int) = context.dataStore.edit { it[Keys.THREAD_COUNT] = n }
    suspend fun setFlashAttention(v: Boolean) = context.dataStore.edit { it[Keys.FLASH_ATTENTION] = v }
    suspend fun setStreamingOutput(v: Boolean) = context.dataStore.edit { it[Keys.STREAMING_OUTPUT] = v }
    suspend fun setShowThinking(v: Boolean) = context.dataStore.edit { it[Keys.SHOW_THINKING] = v }
    suspend fun setMaxHistory(n: Int) = context.dataStore.edit { it[Keys.MAX_HISTORY] = n }
    suspend fun setModelsDir(d: String) = context.dataStore.edit { it[Keys.MODELS_DIR] = d }
    suspend fun setCloudInference(v: Boolean) = context.dataStore.edit { it[Keys.CLOUD_INFERENCE] = v }
    suspend fun setDefaultProvider(p: String) = context.dataStore.edit { it[Keys.DEFAULT_PROVIDER] = p }
    suspend fun setDarkTheme(v: Boolean) = context.dataStore.edit { it[Keys.DARK_THEME] = v }
    suspend fun setProviderStrategy(s: String) = context.dataStore.edit { it[Keys.PROVIDER_STRATEGY] = s }
    suspend fun setDeveloperMode(v: Boolean) = context.dataStore.edit { it[Keys.DEVELOPER_MODE] = v }
    suspend fun setOnboardingDone(v: Boolean) = context.dataStore.edit { it[Keys.ONBOARDING_DONE] = v }
}
