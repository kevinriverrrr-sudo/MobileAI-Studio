package com.mobileaistudio.ui.screens.onboarding

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import com.mobileaistudio.ui.navigation.Screen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileaistudio.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _tokenValid = MutableStateFlow(false)
    val tokenValid: StateFlow<Boolean> = _tokenValid

    private val _tokenChecking = MutableStateFlow(false)
    val tokenChecking: StateFlow<Boolean> = _tokenChecking

    private val _tokenSaved = MutableStateFlow(false)
    val tokenSaved: StateFlow<Boolean> = _tokenSaved

    fun validateToken(token: String): Boolean {
        val valid = token.startsWith("hf_") && token.length > 10
        _tokenValid.value = valid
        return valid
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            userPreferences.setHfToken(token)
            userPreferences.setOnboardingDone(true)
            _tokenSaved.value = true
        }
    }

    fun markOnboardingDone() {
        viewModelScope.launch {
            userPreferences.setOnboardingDone(true)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavController, viewModel: OnboardingViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var token by remember { mutableStateOf("") }
    val tokenValid by viewModel.tokenValid.collectAsState()
    val tokenChecking by viewModel.tokenChecking.collectAsState()
    val tokenSaved by viewModel.tokenSaved.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    // Auto-advance when token is saved
    LaunchedEffect(tokenSaved) {
        if (tokenSaved) {
            pagerState.animateScrollToPage(2)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = false
    ) { page ->
        when (page) {
            0 -> OnboardingPageWelcome(onNext = { scope.launch { pagerState.animateScrollToPage(1) } })
            1 -> OnboardingPageHuggingFace(
                token = token,
                onTokenChange = { token = it },
                tokenChecking = tokenChecking,
                tokenValid = tokenValid,
                onCheckToken = {
                    if (viewModel.validateToken(token)) {
                        viewModel.saveToken(token)
                    }
                },
                onSkip = {
                    viewModel.markOnboardingDone()
                    scope.launch { pagerState.animateScrollToPage(2) }
                }
            )
            2 -> OnboardingPagePermissions(
                onDone = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 48.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(8.dp)
                    .background(
                        color = if (pagerState.currentPage == index)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

@Composable
fun OnboardingPageWelcome(onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🤖", style = MaterialTheme.typography.displayLarge)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Добро пожаловать в\nMobileAI Studio",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Скачивай, настраивай и общайся с LLM-моделями\nлокально на твоём устройстве.\nНикаких ограничений, полностью приватно.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth(0.8f)) {
            Text("Далее")
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
fun OnboardingPageHuggingFace(
    token: String,
    onTokenChange: (String) -> Unit,
    tokenChecking: Boolean,
    tokenValid: Boolean,
    onCheckToken: () -> Unit,
    onSkip: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Cloud, contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))
        Text("Подключи HuggingFace",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Миллионы моделей — один ключ",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = token,
            onValueChange = onTokenChange,
            label = { Text("HuggingFace API Token") },
            placeholder = { Text("hf_...") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                if (tokenValid) Icon(Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary)
            }
        )
        TextButton(onClick = {
            context.startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("https://huggingface.co/settings/tokens")))
        }) {
            Text("Получить ключ")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onCheckToken,
            modifier = Modifier.fillMaxWidth(),
            enabled = token.isNotBlank()
        ) {
            if (tokenChecking) CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            ) else Text("Сохранить и продолжить")
        }
        TextButton(onClick = onSkip) {
            Text("Пропустить", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun OnboardingPagePermissions(onDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Security, contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))
        Text("Готово к запуску!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text("MobileAI Studio полностью приватное приложение.\nНикакие данные не покидают ваше устройство.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = onDone, modifier = Modifier.fillMaxWidth(0.8f)) {
            Text("Начать")
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.RocketLaunch, contentDescription = null)
        }
    }
}
