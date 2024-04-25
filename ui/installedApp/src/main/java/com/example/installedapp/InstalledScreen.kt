package com.example.installedapp

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun InstalledScreen (
    viewModel: InstalledViewModel = viewModel(),
    modifier: Modifier = Modifier
){
    val apps by viewModel.installedUiState.collectAsState()

    LazyColumn(modifier = modifier) {
        items(apps.nameList.size) { index -> // 인덱스를 사용해 리스트의 항목에 접근
            Text(text = apps.nameList[index], modifier = Modifier.padding(8.dp))
        }
    }
}