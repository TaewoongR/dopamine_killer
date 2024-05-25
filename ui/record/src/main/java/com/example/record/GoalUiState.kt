package com.example.record

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig

data class GoalUiState(
    val goalList: List<GoalUiInfo> = listOf()
)

data class GoalUiInfo(
    val appName: String = "",
    val appIcon: ImageBitmap = ImageBitmap(1,1, ImageBitmapConfig.Alpha8),
    val date: String = "",
    val goalTime: Int = 0
)