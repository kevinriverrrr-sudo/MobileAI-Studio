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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileaistudio.domain.model.GGUFVariant
import com.mobileaistudio.domain.repository.IModelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModelDetailViewModel @Inject constructor(
    private val modelRepository: IModelRepository
) : ViewModel() {
    private val _variants = MutableStateFlow<List<GGUFVariant>>(emptyList())
    val variants: StateFlow<List<GGUFVariant>> = _variants

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _downloadStarted = MutableStateFlow<String?>(null)
    val downloadStarted: StateFlow<String?> = _downloadStarted

    fun loadModelFiles(repoId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _variants.value = modelRepository.getModelDetails(repoId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Ошибка загрузки файлов"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun startDownload(variant: GGUFVariant, context: android.content.Context) {
        _downloadStarted.value = variant.fileName

        // Extract repo ID from download URL
        val repoId = variant.downloadUrl
            .substringAfter("huggingface.co/")
            .substringBefore("/resolve")

        // Start foreground download service
        com.mobileaistudio.service.DownloadService.startDownload(
            context = context,
            modelName = variant.fileName.replace(".gguf", ""),
            downloadUrl = variant.downloadUrl,
            fileName = variant.fileName
        )

        // Save model entry to DB
        viewModelScope.launch {
            try {
                val model = com.mobileaistudio.domain.model.AIModel(
                    id = java.util.UUID.randomUUID().toString(),
                    repoId = repoId,
                    fileName = variant.fileName,
                    displayName = variant.fileName.replace(".gguf", ""),
                    author = repoId.split("/").firstOrNull() ?: "",
                    quantization = variant.quantization,
                    fileSizeBytes = variant.fileSizeBytes,
                    filePath = "models/${variant.fileName}"
                )
                modelRepository.saveModel(model)
            } catch (e: Exception) {
                // Log but don't fail
            }
            kotlinx.coroutines.delay(1500)
            _downloadStarted.value = null
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelDetailScreen(
    navController: NavController,
    repoId: String,
    viewModel: ModelDetailViewModel = hiltViewModel()
) {
    val variants by viewModel.variants.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val downloadStarted by viewModel.downloadStarted.collectAsState()

    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(repoId) {
        viewModel.loadModelFiles(repoId)
    }

    val repoName = repoId.split("/").lastOrNull() ?: repoId
    val authorName = repoId.split("/").firstOrNull() ?: ""

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(repoName) },
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
            Text(repoName, style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold)
            Text("Автор: $authorName",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))

            Card(colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("GGUF варианты скачивания",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Выберите квантизацию, подходящую вашему устройству",
                        style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("Доступные файлы",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Card(modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(error!!, color = MaterialTheme.colorScheme.onErrorContainer)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadModelFiles(repoId) }) {
                            Text("Повторить")
                        }
                    }
                }
            } else if (variants.isEmpty()) {
                Text("GGUF файлы не найдены",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                variants.forEach { variant ->
                    val isDownloading = downloadStarted == variant.fileName
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(variant.quantization, fontWeight = FontWeight.SemiBold)
                                Text(
                                    "${formatFileSize(variant.fileSizeBytes)} • RAM: ~${formatFileSize(variant.estimatedRAM)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    variant.fileName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                            if (isDownloading) {
                                CircularProgressIndicator(modifier = Modifier.size(32.dp),
                                    strokeWidth = 3.dp)
                            } else {
                                Button(
                                    onClick = { viewModel.startDownload(variant, context) },
                                    enabled = !isDownloading
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null,
                                        modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Скачать")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

fun formatFileSize(bytes: Long): String = when {
    bytes >= 1_073_741_824 -> "%.1f ГБ".format(bytes / 1_073_741_824.0)
    bytes >= 1_048_576 -> "%.0f МБ".format(bytes / 1_048_576.0)
    bytes >= 1_024 -> "%.0f КБ".format(bytes / 1_024.0)
    else -> "$bytes Б"
}
