package com.mobileaistudio.ui.navigation

import android.net.Uri
import androidx.navigation.NavType
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Main : Screen("main")
    data object Chat : Screen("chat/{chatId}") {
        fun createRoute(chatId: String = "new") = "chat/$chatId"
    }
    data object ModelDetail : Screen("model_detail/{repoId}") {
        fun createRoute(repoId: String) = "model_detail/${Uri.encode(repoId)}"
    }
    data object Discover : Screen("discover")
    data object MyModels : Screen("my_models")
    data object Settings : Screen("settings")
    data object HardwareInfo : Screen("hardware_info")
}
