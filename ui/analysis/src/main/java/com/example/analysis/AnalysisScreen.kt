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
import com.example.installedapp.InstalledScreen

@Composable
fun AnalysisScreen(
    viewModel: AnalysisViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val appUiState by viewModel.appUiState.collectAsState()

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(modifier = modifier.padding(16.dp)) {
            Column {
                AppUsageItem(appUsage = appUiState)
                Button(onClick = { viewModel.updateHourlyData() }) {
                    Text("Load App Usage Data")
                }
            }
            // 여기서 두 번째 Column은 자연스럽게 아래에 배치됩니다.
            InstalledScreen()
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
