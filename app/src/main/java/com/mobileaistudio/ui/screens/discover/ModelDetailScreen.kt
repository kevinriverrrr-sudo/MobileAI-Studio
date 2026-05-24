package com.mobileaistudio.ui.screens.discover

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelDetailScreen(
    navController: NavController,
    repoId: String
) {
    // Simplified detail screen
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(repoId.split("/").lastOrNull() ?: repoId) },
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
                .padding(16.dp)
        ) {
            Text(repoId, style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold)
            Text("Автор: ${repoId.split("/").firstOrNull() ?: ""}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
            Card(colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Совместимость с вашим устройством",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Проверьте размеры GGUF файлов ниже",
                        style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Варианты скачивания",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            val quants = listOf(
                Triple("Q4_K_M", "4.7 ГБ", "~5.5 ГБ RAM"),
                Triple("Q5_K_M", "5.4 ГБ", "~6.5 ГБ RAM"),
                Triple("Q6_K", "6.7 ГБ", "~8.0 ГБ RAM"),
                Triple("Q8_0", "8.5 ГБ", "~10.0 ГБ RAM")
            )

            quants.forEach { (quant, size, ram) ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(quant, fontWeight = FontWeight.SemiBold)
                            Text("$size • RAM: $ram",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Button(onClick = { /* Download */ }) {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Скачать")
                        }
                    }
                }
            }
        }
    }
}
