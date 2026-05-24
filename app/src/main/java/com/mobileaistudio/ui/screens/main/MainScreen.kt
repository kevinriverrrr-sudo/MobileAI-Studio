package com.mobileaistudio.ui.screens.main

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mobileaistudio.ui.navigation.Screen

@Composable
fun MainScreen(navController: NavController) {
    val items = listOf(
        Triple(Screen.Chat.route, "Чат", Icons.Default.ChatBubbleOutline),
        Triple(Screen.Discover.route, "Обзор", Icons.Default.Explore),
        Triple(Screen.MyModels.route, "Модели", Icons.Default.Folder),
        Triple(Screen.Settings.route, "Настройки", Icons.Default.Settings)
    )

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, (route, label, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            navController.navigate(route) {
                                popUpTo(Screen.Main.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        // Content handled by navigation within each tab
        // The main screen simply serves as the container with bottom nav
        when (selectedTab) {
            0 -> {
                // Navigate to chat
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Chat.createRoute("new")) {
                        popUpTo(Screen.Main.route) { saveState = true }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.padding(paddingValues))
    }
}
