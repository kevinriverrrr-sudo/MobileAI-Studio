package com.mobileaistudio.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.mobileaistudio.ui.screens.chat.ChatTabContent
import com.mobileaistudio.ui.screens.discover.DiscoverTabContent
import com.mobileaistudio.ui.screens.mymodels.MyModelsTabContent
import com.mobileaistudio.ui.screens.settings.SettingsTabContent

data class TabItem(
    val label: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        TabItem("Чат", Icons.Default.Chat),
        TabItem("Обзор", Icons.Default.Explore),
        TabItem("Модели", Icons.Default.Folder),
        TabItem("Настройки", Icons.Default.Settings)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }
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
