package com.example.analysis

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.local.AppData

@Composable
fun AnalysisScreen(viewModel: AnalysisViewModel = hiltViewModel()) {
    // ViewModel에서 어플 사용량 데이터를 관찰
    val appUsageData by viewModel.appUsageData.collectAsState()

    // LazyColumn을 사용하여 리스트 형태로 데이터 표시
    LazyColumn {
        items(appUsageData, itemContent = { appUsage: AppData ->
            AppUsageItem(appUsage)
        })
    }
}

@Composable
fun AppUsageItem(appUsage: AppData) {
    // 각 어플 사용량 데이터 항목을 표시하는 UI 구성
    // 예시로, Text Composable을 사용하여 간단하게 어플 이름과 사용 시간을 표시
    Text(text = "${appUsage.appName}: ${appUsage.appTime}시간")
}

