package com.example.analysis

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig

data class AnalysisUiState(
    val appList: List<AnalysisAppData> = listOf(),
)

data class AnalysisAppData(
    val appName: String = "",
    val dailyTime: Int = 0,
    val yesterdayTime: Int = 0,
    val lastWeekAvgTime: Int = 0,
    val lastMonthAvgTime: Int = 0,
    val appIcon: ImageBitmap = ImageBitmap(1,1, ImageBitmapConfig.Alpha8)
)