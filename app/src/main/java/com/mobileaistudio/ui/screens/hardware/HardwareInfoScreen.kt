package com.mobileaistudio.ui.screens.hardware

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HardwareInfoScreen(
    navController: NavController,
    viewModel: HardwareViewModel = hiltViewModel()
) {
    val device by viewModel.deviceCapabilities.collectAsState()
    val error by viewModel.error.collectAsState()
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(device) { if (device != null) isLoading = false }
    LaunchedEffect(Unit) { viewModel.detect() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Информация об устройстве") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            if (error != null) {
                Card(modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(error.orEmpty(),
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }

            InfoCard(Icons.Default.Memory, "Процессор", listOf(
                "Модель: ${device?.cpuModel ?: "Не определён"}",
                "Ядра: ${device?.cpuCores ?: "N/A"}",
                "Архитектура: ${device?.cpuArchitecture ?: "N/A"}",
                "NEON: ${if (device?.neonSupported == true) "Да" else "Нет"}"
            ))

            InfoCard(Icons.Default.Storage, "Память (RAM)", listOf(
                "Всего: ${"%.1f".format(device?.totalRamGB ?: 0f)} ГБ",
                "Доступно: ${"%.1f".format(device?.availableRamGB ?: 0f)} ГБ"
            ))

            InfoCard(Icons.Default.Memory, "GPU", listOf(
                "Модель: ${device?.gpuModel ?: "Не определена"}",
                "OpenGL: ${device?.openGLVersion ?: "N/A"}",
                "Vulkan: ${device?.vulkanVersion ?: "Не поддерживается"}",
                "Vulkan Compute: ${if (device?.vulkanComputeSupported == true) "Да" else "Нет"}"
            ))

            InfoCard(Icons.Default.Folder, "Хранилище", listOf(
                "Всего: ${"%.1f".format(device?.totalStorageGB ?: 0f)} ГБ",
                "Свободно: ${"%.1f".format(device?.freeStorageGB ?: 0f)} ГБ"
            ))

            InfoCard(Icons.Default.DeviceUnknown, "Устройство", listOf(
                "Модель: ${device?.deviceName ?: "N/A"}",
                "Производитель: ${device?.deviceManufacturer ?: "N/A"}",
                "Android: ${device?.androidVersion ?: "N/A"}",
                "SDK: ${device?.sdkVersion ?: "N/A"}"
            ))

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    items: List<String>
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            items.forEach { item ->
                Text(item, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
