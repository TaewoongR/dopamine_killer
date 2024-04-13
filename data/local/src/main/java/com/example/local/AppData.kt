package com.example.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_data")
data class AppData (
    @PrimaryKey val appName: String,
    val appTime: IntArray = IntArray(24),
    var isCompleted: Boolean,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AppData

        return appTime.contentEquals(other.appTime)
    }

    override fun hashCode(): Int {
        return appTime.contentHashCode()
    }
}