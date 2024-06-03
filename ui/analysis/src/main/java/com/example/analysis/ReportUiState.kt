package com.example.analysis

data class ReportUiState (
    val hourlyList: List<Int> = listOf(30),
    val dailyList: List<Int> = listOf(24)
)