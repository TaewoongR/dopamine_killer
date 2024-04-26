package com.example.reward

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun RewardScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Text(
        text = "reward",
        modifier = modifier
    )
}