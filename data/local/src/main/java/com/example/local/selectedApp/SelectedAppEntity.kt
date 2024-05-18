package com.example.local.selectedApp

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "selected_app",
    indices = [Index(value = ["appName"], unique = true)]       // appName 중복방지
)
data class SelectedAppEntity(
    @PrimaryKey
    val appName: String ="",
    val packageName: String ="",
    val isSelected: Boolean = false,
)