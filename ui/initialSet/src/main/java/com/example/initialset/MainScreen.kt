package com.example.initialset

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.navigation.BotNavBar
import com.example.record.GoalSettingScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "app_setting") {
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
