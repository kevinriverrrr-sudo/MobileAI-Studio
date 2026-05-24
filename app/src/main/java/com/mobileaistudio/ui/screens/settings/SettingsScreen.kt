package com.mobileaistudio.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileaistudio.data.local.UserPreferences
import com.mobileaistudio.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    val wifiOnly: StateFlow<Boolean> = userPreferences.wifiOnly
        .let { flow ->
            kotlinx.coroutines.flow.MutableStateFlow(false).also { state ->
                viewModelScope.launch { state.value = flow.first() }
                viewModelScope.launch { flow.collect { state.value = it } }
            }
        }

    val cloudInference: StateFlow<Boolean> = userPreferences.cloudInference
        .let { flow ->
            kotlinx.coroutines.flow.MutableStateFlow(false).also { state ->
                viewModelScope.launch { state.value = flow.first() }
                viewModelScope.launch { flow.collect { state.value = it } }
            }
        }

    val streamingOutput: StateFlow<Boolean> = userPreferences.streamingOutput
        .let { flow ->
            kotlinx.coroutines.flow.MutableStateFlow(true).also { state ->
                viewModelScope.launch { state.value = flow.first() }
                viewModelScope.launch { flow.collect { state.value = it } }
            }
        }

    val showThinking: StateFlow<Boolean> = userPreferences.showThinking
        .let { flow ->
            kotlinx.coroutines.flow.MutableStateFlow(true).also { state ->
                viewModelScope.launch { state.value = flow.first() }
                viewModelScope.launch { flow.collect { state.value = it } }
            }
        }

    val autoGpuOffload: StateFlow<Boolean> = userPreferences.autoGpuOffload
        .let { flow ->
            kotlinx.coroutines.flow.MutableStateFlow(true).also { state ->
                viewModelScope.launch { state.value = flow.first() }
                viewModelScope.launch { flow.collect { state.value = it } }
            }
        }

    val flashAttention: StateFlow<Boolean> = userPreferences.flashAttention
        .let { flow ->
            kotlinx.coroutines.flow.MutableStateFlow(true).also { state ->
                viewModelScope.launch { state.value = flow.first() }
                viewModelScope.launch { flow.collect { state.value = it } }
            }
        }

    private val _hfToken = kotlinx.coroutines.flow.MutableStateFlow("")
    val hfToken: StateFlow<String> = _hfToken

    init {
        viewModelScope.launch { _hfToken.value = userPreferences.hfToken.first() }
    }

    fun setWifiOnly(v: Boolean) = viewModelScope.launch { userPreferences.setWifiOnly(v) }
    fun setCloudInference(v: Boolean) = viewModelScope.launch { userPreferences.setCloudInference(v) }
    fun setStreamingOutput(v: Boolean) = viewModelScope.launch { userPreferences.setStreamingOutput(v) }
    fun setShowThinking(v: Boolean) = viewModelScope.launch { userPreferences.setShowThinking(v) }
    fun setAutoGpuOffload(v: Boolean) = viewModelScope.launch { userPreferences.setAutoGpuOffload(v) }
    fun setFlashAttention(v: Boolean) = viewModelScope.launch { userPreferences.setFlashAttention(v) }
    fun setHfToken(t: String) = viewModelScope.launch { userPreferences.setHfToken(t) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel = hiltViewModel()) {
    val wifiOnly by viewModel.wifiOnly.collectAsState()
    val cloudInference by viewModel.cloudInference.collectAsState()
    val streamingOutput by viewModel.streamingOutput.collectAsState()
    val showThinking by viewModel.showThinking.collectAsState()
    val autoGpuOffload by viewModel.autoGpuOffload.collectAsState()
    val flashAttention by viewModel.flashAttention.collectAsState()
    val hfToken by viewModel.hfToken.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = { Text("Настройки", fontWeight = FontWeight.Bold) }
        )

        // HuggingFace section
        Text("HuggingFace", style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.primary)

        SettingItem(icon = Icons.Default.Key, title = "API Token",
            subtitle = if (hfToken.isNotEmpty()) "${hfToken.take(8)}...${hfToken.takeLast(4)}" else "Не задан")
        SettingItem(icon = Icons.Default.Person, title = "Аккаунт",
            subtitle = "Подключён через токен")
        SettingSwitch(title = "Облачная инференс", subtitle = "Использовать HF Providers",
            checked = cloudInference, onCheckedChange = { viewModel.setCloudInference(it) })
        SettingItem(icon = Icons.Default.Speed, title = "Провайдер",
            subtitle = "Авто (самый быстрый)")

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Download section
        Text("Скачивание", style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.primary)

        SettingSwitch(title = "Только по Wi-Fi", subtitle = "Не качать через мобильную сеть",
            checked = wifiOnly, onCheckedChange = { viewModel.setWifiOnly(it) })
        SettingItem(icon = Icons.Default.Folder, title = "Папка для моделей",
            subtitle = "/storage/emulated/0/MobileAI/models/")

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Inference section
        Text("Инференс", style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.primary)

        SettingItem(icon = Icons.Default.Memory, title = "GPU Backend",
            subtitle = "Vulkan (рекомендуется)")
        SettingSwitch(title = "Авто GPU Offload", subtitle = "Автоматическая настройка слоёв",
            checked = autoGpuOffload, onCheckedChange = { viewModel.setAutoGpuOffload(it) })
        SettingSwitch(title = "Flash Attention", subtitle = "Ускорение внимания",
            checked = flashAttention, onCheckedChange = { viewModel.setFlashAttention(it) })

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Chat section
        Text("Чат", style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.primary)

        SettingSwitch(title = "Потоковый вывод", subtitle = "Отображать ответ по мере генерации",
            checked = streamingOutput, onCheckedChange = { viewModel.setStreamingOutput(it) })
        SettingSwitch(title = "Блок размышлений", subtitle = "Показывать ход мыслей модели",
            checked = showThinking, onCheckedChange = { viewModel.setShowThinking(it) })

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Appearance
        Text("Внешний вид", style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.primary)

        SettingItem(icon = Icons.Default.Palette, title = "Тема",
            subtitle = "Системная")
        SettingItem(icon = Icons.Default.Language, title = "Язык",
            subtitle = "Русский")

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Other
        SettingItem(icon = Icons.Default.Info, title = "Об устройстве",
            subtitle = "CPU, GPU, RAM",
            onClick = { navController.navigate(Screen.HardwareInfo.route) })
        SettingItem(icon = Icons.Default.Info, title = "О приложении",
            subtitle = "MobileAI Studio v1.0.0")

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = { Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
fun SettingSwitch(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        trailingContent = {
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    )
}
