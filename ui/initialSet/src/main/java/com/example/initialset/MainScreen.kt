package com.example.initialset

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myinfo.AuthSelectionScreen
import com.example.myinfo.LoginScreen
import com.example.myinfo.SignUpScreen
import com.example.navigation.BotNavBar
import com.example.record.GoalSettingScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "auth_selection_route") {
        composable("auth_selection_route"){
            AuthSelectionScreen(navController = navController)
        }
        composable("login_route"){
            val navigateToMainScreen = {
                navController.navigate("app_setting"){
                    popUpTo("auth_selection_route"){inclusive = true}
                }
            }
            LoginScreen(navController, navigateToMainScreen)
        }
        composable("signup_route"){
            SignUpScreen(navController)
        }
        composable("app_setting") {
            AppSettingScreen(navController)
        }
        composable("goal_setting") {
            GoalSettingScreen(navController)
        }
        composable("bot_nav_bar") {
            BotNavBar()
        }
        // 다른 navigation 설정...
    }
}
