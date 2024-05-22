package com.example.navigation

import android.app.AppOpsManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.local.user.UserTokenStore
import com.example.myinfo.AuthSelectionScreen
import com.example.myinfo.LoginScreen
import com.example.myinfo.SignUpScreen
import com.example.navigation.botNav.BotNavBar
import com.example.navigation.initialSetting.AppSettingScreen
import com.example.navigation.initialSetting.GoalSettingScreen
import com.example.navigation.initialSetting.PermissionScreen
import com.example.navigation.setup.SetupFlag

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
    val hasPermission = remember { mutableStateOf(false) }

    // 기본 startDestination을 설정합니다.
    val startDestination = "placeholder_start" // 임시로 플레이스홀더 설정

    // LaunchedEffect를 사용하여 토큰 상태를 다시 확인하고 적절한 화면으로 네비게이션
    LaunchedEffect(Unit) {
        //PreferenceUtils.resetSetup(context)         // 테스트 재시작시 필요
        //TokenManager.clearToken(context)            // 테스트 재시작시 필요
        val currentToken = UserTokenStore.getToken(context)
        if (currentToken != null) {
            navController.navigate("bot_nav_bar") {
                popUpTo(0) { inclusive = true }
            }
        } else {
            navController.navigate("auth_selection_route") {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
            }
        }
    }


    NavHost(navController = navController, startDestination = startDestination) {
        composable("placeholder_start"){
            // LaunchedEffect를 사용하여 토큰 상태를 다시 확인하고 적절한 화면으로 네비게이션
        }
        composable("auth_selection_route"){
            AuthSelectionScreen(navController)
        }
        composable("login_route"){
            val navigateToScreen: () -> Unit = {
                if (mode == AppOpsManager.MODE_ALLOWED) {
                    if (SetupFlag.isSetupComplete(context)) {
                        navController.navigate("bot_nav_bar") {
                            popUpTo(0) { inclusive = true }
                        }
                    } else {
                        navController.navigate("app_setting") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                } else {
                    navController.navigate("permission_screen") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            LoginScreen(navController, navigateToScreen)
        }
        composable("signup_route"){
            SignUpScreen(navController)
        }
        composable("permission_screen"){
            hasPermission.value = (mode == AppOpsManager.MODE_ALLOWED)
            if (hasPermission.value) {
                AppSettingScreen(navController)
            } else
                PermissionScreen(navController)
        }
        composable("app_setting") {
            AppSettingScreen(navController)
        }
        composable("goal_setting") {
            SetupFlag.saveSetupComplete(context)
            GoalSettingScreen(navController)
        }
        composable("bot_nav_bar") {
            BotNavBar()
        }
    }
}


