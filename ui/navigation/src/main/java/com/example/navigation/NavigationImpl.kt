package com.example.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import javax.inject.Inject

class NavigationImpl @Inject constructor(private val navController: NavController) : Navigation {
    @Composable
    override fun navigateTo(screen: Screens) {
        when (screen) {
            Screens.OVERVIEW -> navController.navigate("overview")
            Screens.ANALYSIS -> navController.navigate("analysis")
        }
    }
}
