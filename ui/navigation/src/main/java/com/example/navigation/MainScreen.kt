package com.example.navigation

import android.app.AppOpsManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.initialset.AppSettingScreen
import com.example.myinfo.AuthSelectionScreen
import com.example.myinfo.LoginScreen
import com.example.myinfo.SignUpScreen
import com.example.navigation.util.PreferenceUtils
import com.example.record.GoalSettingScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "auth_selection_route") {
        composable("auth_selection_route"){
            AuthSelectionScreen(navController)
        }
        composable("login_route"){
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
            val navigateToScreen: () -> Unit = {
                if (mode == AppOpsManager.MODE_ALLOWED) {
                    if (PreferenceUtils.isSetupComplete(context)) {
                        navController.navigate("bot_nav_bar") {
                            popUpTo("login_route") { inclusive = true }
                        }
                    } else {
                        navController.navigate("app_setting") {
                            popUpTo("login_route") { inclusive = true }
                        }
                    }
                } else {
                    navController.navigate("permission_screen") {
                        popUpTo("login_route") { inclusive = true }
                    }
                }
            }
            PreferenceUtils.resetSetup(context)
            LoginScreen(navController, navigateToScreen)
        }
        composable("signup_route"){
            SignUpScreen(navController)
        }
        composable("permission_screen"){
            PermissionScreen(navController)
        }
        composable("app_setting") {
            AppSettingScreen(navController)
        }
        composable("goal_setting") {
            PreferenceUtils.saveSetupComplete(context)
            GoalSettingScreen(navController)
        }
        composable("bot_nav_bar") {
            BotNavBar()
        }
    }
}


