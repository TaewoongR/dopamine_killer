package com.example.dopamine_killer

import androidx.navigation.NavHostController
import com.example.dopamine_killer.EntireScreens.ANALYSIS_SCREEN
import com.example.dopamine_killer.EntireScreens.INSTALLED_SCREEN
import com.example.dopamine_killer.EntireScreens.MYINFO_SCREEN
import com.example.dopamine_killer.EntireScreens.OVERVIEW_SCREEN
import com.example.dopamine_killer.EntireScreens.PERMISSION_SCREEN

private object EntireScreens {
    const val PERMISSION_SCREEN = "permission"
    const val ANALYSIS_SCREEN = "analysis"
    const val OVERVIEW_SCREEN = "overview"
    const val INSTALLED_SCREEN = "installed"
    const val MYINFO_SCREEN = "myinfo"
}

object Destinations {
    const val PERMISSION_ROUTE = PERMISSION_SCREEN
    const val ANALYSIS_ROUTE = ANALYSIS_SCREEN
    const val OVERVIEW_ROUTE = OVERVIEW_SCREEN
    const val INSTALLED_ROUTE = INSTALLED_SCREEN
    const val MYINFO_ROUTE = MYINFO_SCREEN
}

/**
 * Models the navigation actions in the app.
 */
class NavigationActions(private val navController: NavHostController) {

    fun navigateToPermission(){
        navController.navigate(Destinations.PERMISSION_ROUTE)
    }

    fun navigateToOverview() {
        navController.navigate(Destinations.OVERVIEW_ROUTE)
    }

    fun navigateToInstalled() {
        navController.navigate(Destinations.INSTALLED_ROUTE)
    }

    fun navigateToAnalysis() {
        navController.navigate(Destinations.ANALYSIS_ROUTE)
    }

    fun navigateToMyinfo(){
        navController.navigate(Destinations.MYINFO_ROUTE)
    }
}