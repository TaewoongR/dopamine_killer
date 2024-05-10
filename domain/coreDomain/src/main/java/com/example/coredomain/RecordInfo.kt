package com.example.coredomain

import androidx.compose.ui.graphics.ImageBitmap

data class RecordInfo(
    val appName: String,
    val appImage: ImageBitmap,
    val date: String,
    val goalTime: Int,
    val howLong: Int,
    val onGoing: Boolean
)
