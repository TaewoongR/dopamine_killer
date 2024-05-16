package com.example.navigation.initialSetting

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig

data class AppSettingUiState(
    val appList: List<AppSettingData> = listOf()
)

data class AppSettingData(
    val appName: String = "",
    val icon : ImageBitmap = ImageBitmap(1,1, ImageBitmapConfig.Alpha8),
    var isButtonEnabled: Boolean = false  // 토글 상태를 저장하는 필드 추가
)