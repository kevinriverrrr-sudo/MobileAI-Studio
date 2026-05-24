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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mobileaistudio.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Настройки") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // HuggingFace section
            Text("HuggingFace", style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.primary)

            SettingItem(icon = Icons.Default.Key, title = "API Token",
                subtitle = "Настроить HF токен")
            SettingItem(icon = Icons.Default.Person, title = "Аккаунт",
                subtitle = "Не авторизован")
            SettingSwitch(title = "Облачная инференс", subtitle = "Использовать HF Providers",
                checked = true, onCheckedChange = {})
            SettingItem(icon = Icons.Default.Speed, title = "Провайдер",
                subtitle = "Авто (самый быстрый)")

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Download section
            Text("Скачивание", style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.primary)

            SettingSwitch(title = "Только по Wi-Fi", subtitle = "Не качать через мобильную сеть",
                checked = true, onCheckedChange = {})
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
                checked = true, onCheckedChange = {})
            SettingSwitch(title = "Flash Attention", subtitle = "Ускорение внимания",
                checked = true, onCheckedChange = {})

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Chat section
            Text("Чат", style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.primary)

            SettingSwitch(title = "Потоковый вывод", subtitle = "Отображать ответ по мере генерации",
                checked = true, onCheckedChange = {})
            SettingSwitch(title = "Блок размышлений", subtitle = "Показывать ход мыслей модели",
                checked = true, onCheckedChange = {})

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
            SettingItem(icon = Icons.Default.Code, title = "GitHub",
                subtitle = "Открыть проект")

            Spacer(modifier = Modifier.height(32.dp))
        }
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
