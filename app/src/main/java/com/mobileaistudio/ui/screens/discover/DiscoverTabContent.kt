package com.mobileaistudio.ui.screens.discover

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun DiscoverTabContent(
    navController: NavController,
    viewModel: DiscoverViewModel = hiltViewModel()
) {
    DiscoverScreen(navController = navController, viewModel = viewModel)
}
