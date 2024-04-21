package com.example.analysis

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AnalysisScreen(
    viewModel: AnalysisViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val appUiState by viewModel.appUiState.collectAsState()

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(modifier = modifier.padding(16.dp)) {
            AppUsageItem(appUsage = appUiState)
            // 버튼 클릭 시 ViewModel의 loadAppUsageData() 함수를 호출
            Button(onClick = { viewModel.updateHourlyData() }) {
                Text("Load App Usage Data")
            }
        }
    }
}

@Composable
fun AppUsageItem(appUsage: AnalysisUiState) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "${appUsage.appName}: ${appUsage.dailyTime}초",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "${appUsage.isCompleted}",
            style = MaterialTheme.typography.titleMedium
        )
    }
}
