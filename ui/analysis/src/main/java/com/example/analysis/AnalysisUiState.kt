package com.example.analysis

data class AnalysisUiState(
    val appList: List<AppState> = listOf(),
)

data class AppState(
    val appName: String = "",
    val dailyTime: Int = 0,
    val yesterdayTime: Int = 0,
    val lastWeekAvgTime: Int = 0,
    val lastMonthAvgTime: Int = 0,
)