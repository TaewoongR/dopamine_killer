package com.example.record

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig

data class RecordUiState(
    val recordList: List<RecordDataUi> = listOf()
)

data class RecordDataUi(
    val appName: String = "",
    val appIcon: ImageBitmap = ImageBitmap(1,1, ImageBitmapConfig.Alpha8),
    val date: String = "",
    val goalTime: Int = 0,
    val howLong: Int = 0,
    val onGoing: Boolean = false
)