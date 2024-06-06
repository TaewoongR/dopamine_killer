package com.example.navigation.botNav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Create
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screens(val route : String) {
    data object Overview : Screens("overview_route")
    data object Analysis : Screens("analysis_route")
    data object MyInfo : Screens("myinfo_route")
    data object Reward : Screens("reward_route")
    data object Record : Screens("record_route")
}

data class BottomNavigationItem(
    val label : String = "",
    val icon : ImageVector = Icons.Filled.Home,
    val route : String = ""
) {
    fun bottomNavigationItems() : List<BottomNavigationItem> {
        return listOf(
            BottomNavigationItem(
                label = "목표",
                icon = Icons.Rounded.Create,
                route = Screens.Record.route
            ),
            BottomNavigationItem(
                label = "업적",
                icon = Icons.AutoMirrored.Filled.List,
                route = Screens.Reward.route
            ),
            BottomNavigationItem(
                label = "홈",
                icon = Icons.Filled.Home,
                route = Screens.Overview.route
            ),
            BottomNavigationItem(
                label = "기록",
                icon = Icons.Filled.Search,
                route = Screens.Analysis.route
            ),
            BottomNavigationItem(
                label = "내 정보",
                icon = Icons.Filled.AccountCircle,
                route = Screens.MyInfo.route
            ),
        )
    }
}