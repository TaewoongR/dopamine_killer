package com.example.local.selectedApp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "selected_app")
data class SelectedAppEntity(
    @PrimaryKey
    val appName: String ="",
    val packageName: String ="",
    val isSelected: Boolean = false,
)