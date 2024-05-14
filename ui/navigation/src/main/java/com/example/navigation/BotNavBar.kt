package com.example.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.analysis.AnalysisScreen
import com.example.myinfo.MyInfoScreen
import com.example.overview.OverviewScreen
import com.example.record.RecordScreen
import com.example.reward.RewardScreen

@Composable
fun BotNavBar() {
    var navigationSelectedItem by remember { mutableIntStateOf(2) }
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // material3 하단 네비게이션 바
            NavigationBar {
                BottomNavigationItem().bottomNavigationItems().forEachIndexed { index, navigationItem ->
                        // 하단 네비게이션 바에서 필수적인 요소
                    NavigationBarItem(
                        selected = index == navigationSelectedItem,
                        label = { Text(navigationItem.label) },
                        icon = {
                            Icon(
                                navigationItem.icon,
                                contentDescription = navigationItem.label
                            )
                               },
                        onClick = {
                            navigationSelectedItem = index
                            // NavHost에서 정의한 루트와 연결된 함수로 이동
                            navController.navigate(navigationItem.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
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
                OverviewScreen(navController)
            }
            composable(Screens.Analysis.route) {
                AnalysisScreen(navController)
            }
            composable(Screens.MyInfo.route) {
                MyInfoScreen(navController)
            }
        }
    }
}
