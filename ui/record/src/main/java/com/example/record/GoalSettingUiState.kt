package com.example.record

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig

data class GoalSettingUiState (
    val goalList: List<GoalInfo> = listOf()
)

data class GoalInfo(
    val appName: String = "",
    val appIcon: ImageBitmap = ImageBitmap(1,1, ImageBitmapConfig.Alpha8),
    val date: String = "",
    val goalTime: Int = 0
)
