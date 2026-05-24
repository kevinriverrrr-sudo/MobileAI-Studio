package com.mobileaistudio.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mobileaistudio.domain.model.ChatMessage
import com.mobileaistudio.domain.model.MessageRole
import com.mobileaistudio.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    chatId: String,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val currentModel by viewModel.currentModel.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val tokensPerSec by viewModel.tokensPerSec.collectAsState()
    val error by viewModel.error.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LaunchedEffect(chatId) {
        viewModel.loadChat(chatId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                currentModel?.displayName ?: "Выберите модель",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                modifier = Modifier.size(8.dp),
                                shape = RoundedCornerShape(4.dp),
                                color = if (currentModel != null) CompatibleGreen
                                else IncompatibleRed
                            ) {}
                        }
                        if (isGenerating) {
                            Text(
                                "$tokensPerSec t/s",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.createNewChat() }) {
                        Icon(Icons.Default.Add, "Новый чат")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            // Error banner
            if (error != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        error!!,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Messages list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (messages.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "🧠",
                                    fontSize = 48.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    "Начните разговор",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "Отправьте сообщение для начала",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                items(messages, key = { it.id }) { message ->
                    MessageBubble(message = message, onCopy = { viewModel.copyMessage(it) })
                }

                if (isGenerating) {
                    item {
                        Row(modifier = Modifier.padding(8.dp)) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Генерирует ответ...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Input bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 3.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .imePadding(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    IconButton(onClick = { /* Attach file */ }) {
                        Icon(Icons.Default.AttachFile, "Прикрепить")
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { viewModel.onInputChanged(it) },
                        modifier = Modifier.weight(1f).heightIn(max = 120.dp),
                        placeholder = { Text("Напишите сообщение...") },
                        maxLines = 5,
                        shape = RoundedCornerShape(24.dp)
                    )

                    if (isGenerating) {
                        IconButton(onClick = { viewModel.stopGeneration() }) {
                            Icon(Icons.Default.StopCircle, "Остановить",
                                tint = MaterialTheme.colorScheme.error)
                        }
                    } else {
                        IconButton(
                            onClick = { viewModel.sendMessage() },
                            enabled = inputText.isNotBlank()
                        ) {
                            Icon(Icons.Default.Send, "Отправить",
                                tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessage, onCopy: (String) -> Unit) {
    val isUser = message.role == MessageRole.USER

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Surface(
                modifier = Modifier.size(32.dp).align(Alignment.Top),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🤖", fontSize = 14.sp)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp, topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                color = if (isUser) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isUser) MaterialTheme.colorScheme.onPrimary
                           else MaterialTheme.colorScheme.onSurface
                )
            }

            Row(
                modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isUser) {
                    IconButton(
                        onClick = { onCopy(message.content) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, "Копировать",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (message.tokensPerSecond > 0) {
                        Text(
                            "%.1f t/s".format(message.tokensPerSecond),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (isUser) Spacer(modifier = Modifier.width(8.dp))
    }
}
