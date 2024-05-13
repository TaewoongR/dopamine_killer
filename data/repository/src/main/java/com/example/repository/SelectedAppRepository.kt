package com.example.repository

interface SelectedAppRepository {
    suspend fun getAllInstalled(): List<String>
    suspend fun getAllSelected(): List<String>
    suspend fun updateSelected(appList: List<String>, isSelected: Boolean = false)
}