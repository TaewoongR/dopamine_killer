package com.example.navigation.botNav

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.analysis.AnalysisScreen
import com.example.analysis.ReportScreen
import com.example.myinfo.MyInfoScreen
import com.example.myinfo.SelectedAppEditScreen
import com.example.navigation.MainScreen
import com.example.overview.OverviewScreen
import com.example.record.GoalCreateScreen
import com.example.record.RecordScreen
import com.example.reward.RewardScreen
import kotlinx.coroutines.Job

val keyColor: Color = Color(android.graphics.Color.parseColor("#FF9A62"))

@Composable
fun BotNavBar(
    onCheckPermissions: (Context) -> Unit,
    send2Network: (Any?) -> Unit,
    clearDatabase: () -> Unit,
    startForegroundService: (Any?) -> Unit,
    stopForegroundService: (Any?) -> Unit
) {
    var navigationSelectedItem by remember { mutableIntStateOf(2) }
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // 현재 경로를 가져옴
    val currentRoute = navBackStackEntry?.destination?.route

    // 뒤로가기 할 때 선택된 아이템 업데이트
    navController.addOnDestinationChangedListener { _, destination, _ ->
        BottomNavigationItem().bottomNavigationItems().forEachIndexed { index, navigationItem ->
            if (destination.route == navigationItem.route) {
                navigationSelectedItem = index
            }
        }
    }
    startForegroundService(null)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // main_screen 경로가 아닐 때만 하단 네비게이션 바를 렌더링
            if (currentRoute != "main_screen") {
                NavigationBar(containerColor = Color.White) {
                    BottomNavigationItem().bottomNavigationItems().forEachIndexed { index, navigationItem ->
                        // 하단 네비게이션 바에서 필수적인 요소
                        NavigationBarItem(
                            selected = index == navigationSelectedItem,
                            label = {
                                Text(
                                    navigationItem.label,
                                    fontWeight = FontWeight.Normal
                                )
                            },
                            icon = {
                                Icon(
                                    navigationItem.icon,
                                    contentDescription = navigationItem.label
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = keyColor, // 선택된 아이콘의 색상
                                unselectedIconColor = Color.Gray, // 비선택된 아이콘의 색상
                                selectedTextColor = keyColor, // 선택된 텍스트의 색상
                                unselectedTextColor = Color.Gray, // 비선택된 텍스트의 색상
                                indicatorColor = Color.Transparent// 선택된 아이콘에 생기는 그림자의 색상 = 없음
                            ),
                            onClick = {
                                navigationSelectedItem = index
                                // NavHost에서 정의한 루트와 연결된 함수로 이동
                                navController.navigate(navigationItem.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = false
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screens.Overview.route,
            modifier = Modifier.padding(paddingValues = paddingValues)) {
            composable(Screens.Record.route) {
                RecordScreen(navController)
            }
            composable(Screens.Reward.route) {
                RewardScreen(navController)
            }
            composable(Screens.Overview.route) {
                OverviewScreen(navController, send2Network)
            }
            composable(Screens.Analysis.route) {
                AnalysisScreen(navController)
            }
            composable(Screens.MyInfo.route) {
                MyInfoScreen(navController, onCheckPermissions, clearDatabase, stopForegroundService)
            }
            composable("main_screen"){
                MainScreen(onCheckPermissions, send2Network, startForegroundService, stopForegroundService)
            }
            composable("selected_app_edit"){
                SelectedAppEditScreen(navController)
            }
            composable("goal_create_screen"){
                GoalCreateScreen(navController,)
            }
            composable("report_screen/{appName}"){backStackEntry ->
                val appName = backStackEntry.arguments?.getString("appName") ?: ""
                ReportScreen(navController, appName)
            }

        }
    }
}
