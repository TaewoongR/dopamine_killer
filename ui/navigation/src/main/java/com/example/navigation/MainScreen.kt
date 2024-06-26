package com.example.navigation

import android.app.AppOpsManager
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.local.user.UserTokenStore
import com.example.myinfo.LoginScreen
import com.example.myinfo.SignUpScreen
import com.example.navigation.botNav.BotNavBar
import com.example.navigation.initialSetting.AppSettingScreen
import com.example.navigation.initialSetting.GoalSettingScreen
import com.example.navigation.initialSetting.PermissionScreen
import com.example.myinfo.setup.SetupFlag
import kotlinx.coroutines.launch

@Composable
fun MainScreen(onCheckPermissions: (Context) -> Unit, startForegroundService: (Any?) -> Unit, stopForegroundService: (Any?) -> Unit, viewModel: MainViewModel = hiltViewModel()) {
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val context = LocalContext.current
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
    val hasPermission = remember { mutableStateOf(false) }

    // 기본 startDestination을 설정합니다.
    val startDestination = "placeholder_start" // 임시로 플레이스홀더 설정

    // LaunchedEffect를 사용하여 토큰 상태를 다시 확인하고 적절한 화면으로 네비게이션
    LaunchedEffect(Unit) {
        val currentToken = UserTokenStore.getToken(context)
        if (currentToken != null && SetupFlag.isSetupComplete(context)) {
            navController.navigate("bot_nav_bar") {
                popUpTo(0) { inclusive = true }
            }
        }else if(currentToken != null && mode != AppOpsManager.MODE_ALLOWED){
            navController.navigate("permission_screen"){
                popUpTo(0) { inclusive = true }
            }
        } else {
            navController.navigate("login_route") {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
            }
        }
    }

    val clearDatabase: () -> Unit = {
        scope.launch { viewModel.clearDatabase() }
    }

    val loginUpdate: (String, String) -> Unit = {it1, it2 ->
        scope.launch {
            viewModel.loginUpdate(it1, it2) }
    }

    val initialUpdate: () -> Unit = {
        scope.launch{viewModel.initialUpdate()}
    }

    val postAfterLogin: () -> Unit ={
        scope.launch{viewModel.postAfterLogin(context)}
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("placeholder_start"){
            // LaunchedEffect를 사용하여 토큰 상태를 다시 확인하고 적절한 화면으로 네비게이션
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
            LoginScreen(navController, navigateToScreen, loginUpdate, initialUpdate)
        }
        composable("signup_route"){
            SignUpScreen(navController)
        }
        composable("permission_screen"){
            hasPermission.value = (mode == AppOpsManager.MODE_ALLOWED)
            if (hasPermission.value) {
                AppSettingScreen(navController)
            } else {
                PermissionScreen(navController)
            }
        }
        composable("app_setting") {
            AppSettingScreen(navController)
        }
        composable("goal_setting") {
            val isSettingComplete: () -> Unit = {SetupFlag.saveSetupComplete(context)}
            GoalSettingScreen(navController, isSettingComplete)
        }
        composable("bot_nav_bar") {
            if(!UserTokenStore.getLoginPost(context)) {
                postAfterLogin()
                UserTokenStore.saveLoginPost(context)
            }
            BotNavBar(onCheckPermissions, clearDatabase, startForegroundService, stopForegroundService)
        }
    }
}
