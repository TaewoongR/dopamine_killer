package com.example.installedapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun InstalledScreen (
    viewModel: InstalledViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
){
    val apps by viewModel.installedUiState.collectAsState()

}