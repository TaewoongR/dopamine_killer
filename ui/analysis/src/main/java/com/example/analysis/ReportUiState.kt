package com.example.analysis

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig

data class ReportUiState (
    val hourlyList: List<Int> = listOf(30),
    val dailyList: List<Int> = listOf(24),
    val appIcon: ImageBitmap = ImageBitmap(1,1, ImageBitmapConfig.Alpha8)
)