package com.example.analysis

import AnalysisViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.local.AppData

@Composable
fun AnalysisScreen(
    viewModel: AnalysisViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val appUsageData by viewModel.appUsageData.collectAsState()

    Surface(color = MaterialTheme.colorScheme.background) {
        LazyColumn(modifier.padding(16.dp)) {
            items(appUsageData, key = { it.appName }) { appUsage ->
                AppUsageItem(appUsage)
            }
        }
    }
}

@Composable
fun AppUsageItem(appUsage: AppData) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "${appUsage.appName}: ${appUsage.hour00.toString()}시간",
            style = MaterialTheme.typography.titleMedium
        )
    }
}
