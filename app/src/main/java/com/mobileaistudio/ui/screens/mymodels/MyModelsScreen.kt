package com.mobileaistudio.ui.screens.mymodels

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyModelsScreen(
    navController: NavController,
    viewModel: MyModelsViewModel = hiltViewModel()
) {
    val models by viewModel.models.collectAsState()
    val loadedModel by viewModel.loadedModel.collectAsState()
    val storageUsed by viewModel.storageUsed.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Мои модели (${models.size})")
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Storage bar
            LinearProgressIndicator(
                progress = { storageUsed },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = if (storageUsed > 0.8f) MaterialTheme.colorScheme.error
                       else MaterialTheme.colorScheme.primary
            )

            // Active model
            loadedModel?.let { model ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Активна: ${model.displayName}",
                                fontWeight = FontWeight.SemiBold)
                            Text(model.quantization,
                                style = MaterialTheme.typography.labelSmall)
                        }
                        FilledTonalButton(onClick = { /* Open chat */ }) {
                            Icon(Icons.Default.Chat, null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Чат")
                        }
                    }
                }
            }

            if (models.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.FolderOff, null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Нет скачанных моделей",
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { /* Navigate to discover */ }) {
                            Text("Обзор моделей")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(models) { model ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(40.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(model.author.take(1).uppercase(),
                                            fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(model.displayName, fontWeight = FontWeight.SemiBold,
                                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Row {
                                        Text(model.quantization,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("${model.fileSizeBytes / (1024*1024)} МБ",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                IconButton(onClick = {
                                    viewModel.toggleModelLoaded(model.id)
                                }) {
                                    Icon(
                                        if (model.isLoaded) Icons.Default.StopCircle
                                        else Icons.Default.PlayArrow,
                                        contentDescription = if (model.isLoaded) "Выгрузить"
                                                              else "Загрузить"
                                    )
                                }
                                IconButton(onClick = {
                                    viewModel.deleteModel(model.id)
                                }) {
                                    Icon(Icons.Default.Delete, "Удалить",
                                        tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
