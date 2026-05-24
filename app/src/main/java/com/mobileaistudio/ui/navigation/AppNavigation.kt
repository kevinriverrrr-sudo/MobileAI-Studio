package com.mobileaistudio.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mobileaistudio.ui.screens.chat.ChatScreen
import com.mobileaistudio.ui.screens.discover.ModelDetailScreen
import com.mobileaistudio.ui.screens.hardware.HardwareInfoScreen
import com.mobileaistudio.ui.screens.main.MainScreen
import com.mobileaistudio.ui.screens.onboarding.OnboardingScreen
import com.mobileaistudio.ui.screens.splash.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController = navController)
        }
        composable(Screen.Main.route) {
            MainScreen(navController = navController)
        }
        composable(
            route = Screen.Chat.route,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: "new"
            ChatScreen(navController = navController, chatId = chatId)
        }
        composable(
            route = Screen.ModelDetail.route,
            arguments = listOf(navArgument("repoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val repoId = Uri.decode(backStackEntry.arguments?.getString("repoId") ?: "")
            ModelDetailScreen(navController = navController, repoId = repoId)
        }
        composable(Screen.HardwareInfo.route) {
            HardwareInfoScreen(navController = navController)
        }
    }
}
