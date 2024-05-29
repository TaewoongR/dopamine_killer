package com.example.record

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig

data class RecordUiState(
    val descendingList: List<DescendingRecord> = listOf(),
    val ongoingList: List<OngoingRecord> = listOf(),
    val finishedList: List<FinishedRecord> = listOf()
)

data class DescendingRecord(
    val appName: String,
    val appIcon: ImageBitmap = ImageBitmap(1,1, ImageBitmapConfig.Alpha8),
    val date: String,
    val goalTime: Int,
    val howLong: Int,
    val onGoing: Boolean
)

data class OngoingRecord(
    val appName: String,
    val appIcon: ImageBitmap = ImageBitmap(1,1, ImageBitmapConfig.Alpha8),
    val date: String,
    val goalTime: Int,
    val howLong: Int,
    val todayUsage: Int,
    val onGoing: Boolean
)

data class FinishedRecord(
    val appName: String,
    val appIcon: ImageBitmap = ImageBitmap(1,1, ImageBitmapConfig.Alpha8),
    val date: String,
    val goalTime: Int,
    val howLong: Int,
    val onGoing: Boolean
)