package com.mobileaistudio.ui.screens.mymodels

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun MyModelsTabContent(
    navController: NavController,
    viewModel: MyModelsViewModel = hiltViewModel()
) {
    MyModelsScreen(navController = navController, viewModel = viewModel)
}
