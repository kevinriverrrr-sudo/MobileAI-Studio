package com.mobileaistudio.ui.screens.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsTabContent(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    SettingsScreen(navController = navController, viewModel = viewModel)
}
