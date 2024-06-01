package com.example.recorddomain

import androidx.compose.ui.graphics.ImageBitmap

data class RecordDataDomain(
    val appName: String,
    val appIcon: ImageBitmap,
    val date: String,
    val goalTime: Int,
    val howLong: Int,
    val onGoing: Boolean
)

data class GoalDataDomain(
    val appName: String,
    val date: String,
    val goalTime: Int,
)
