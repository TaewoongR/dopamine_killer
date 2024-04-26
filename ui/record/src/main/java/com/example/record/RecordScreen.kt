package com.example.record

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun RecordScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Record",
        modifier = modifier
    )
}