package com.example.overview

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig

data class OverviewUiState(
    val analysisData: AnalysisData = AnalysisData(),
    val recordList: List<RecordData> = listOf()
)
data class AnalysisData(
    val appName: String = "",
    val goalTime: Int = 0,
    val dailyTime: Int = 0,
    val yesterdayTime: Int = 0,
    val lastWeekAvgTime: Int = 0,
    val lastMonthAvgTime: Int = 0,
    val appIcon: ImageBitmap = ImageBitmap(1,1, ImageBitmapConfig.Alpha8)
)

data class RecordData(
    val appName: String = "",
    val appIcon: ImageBitmap = ImageBitmap(1,1, ImageBitmapConfig.Alpha8),
    val date: String = "",
    val goalTime: Int = 0,
    val howLong: Int = 0,
    val onGoing: Boolean = false
)