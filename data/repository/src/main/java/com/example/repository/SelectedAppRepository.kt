package com.example.repository

import com.example.local.selectedApp.SelectedAppEntity

interface SelectedAppRepository {
    suspend fun getAllInstalled(): List<String>
    suspend fun getAllSelected(): List<String>
    suspend fun updatedInstalled(appNameList: List<String>, isInitial: Boolean)
    suspend fun updateSelected(appList: List<String>)
    suspend fun deleteSelected()
    suspend fun saveSelected(selectedAppEntity: SelectedAppEntity)
}