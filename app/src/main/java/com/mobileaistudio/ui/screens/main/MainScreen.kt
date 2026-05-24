package com.mobileaistudio.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mobileaistudio.ui.navigation.Screen
import com.mobileaistudio.ui.screens.chat.ChatTabContent
import com.mobileaistudio.ui.screens.discover.DiscoverTabContent
import com.mobileaistudio.ui.screens.mymodels.MyModelsTabContent
import com.mobileaistudio.ui.screens.settings.SettingsTabContent

data class TabItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val tabs = listOf(
        TabItem(Screen.Chat.route, "Чат", Icons.Default.ChatBubbleOutline),
        TabItem(Screen.Discover.route, "Обзор", Icons.Default.Explore),
        TabItem(Screen.MyModels.route, "Модели", Icons.Default.Folder),
        TabItem(Screen.Settings.route, "Настройки", Icons.Default.Settings)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine selected tab based on current route
    val selectedTabIndex = remember(currentRoute) {
        when {
            currentRoute == Screen.Chat.route -> 0
            currentRoute?.startsWith(Screen.Chat.route) == true -> 0
            currentRoute == Screen.Discover.route -> 1
            currentRoute == Screen.MyModels.route -> 2
            currentRoute == Screen.Settings.route -> 3
            else -> 0
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        selected = selectedTabIndex == index,
                        onClick = {
                            if (currentRoute != tab.route) {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTabIndex) {
                0 -> ChatTabContent(navController = navController)
                1 -> DiscoverTabContent(navController = navController)
                2 -> MyModelsTabContent(navController = navController)
                3 -> SettingsTabContent(navController = navController)
            }
        }
    }
}
