package com.example.navigation

import androidx.compose.runtime.Composable

enum class Screens {
    OVERVIEW,
    ANALYSIS
}

interface Navigation {
    @Composable
    fun navigateTo(screen: Screens)
}