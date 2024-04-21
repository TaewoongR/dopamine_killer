package com.example.local.app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "app_data",
    /*
    foreignKeys = [
        ForeignKey(
            entity = UserData::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]

     */
)
data class AppData(
    @PrimaryKey
    //val id: String = "",
    //@ColumnInfo(index = true)
    val appName: String,
    val date: String,
    val hour00: Int,
    val hour01: Int,
    val hour02: Int,
    val hour03: Int,
    val hour04: Int,
    val hour05: Int,
    val hour06: Int,
    val hour07: Int,
    val hour08: Int,
    val hour09: Int,
    val hour10: Int,
    val hour11: Int,
    val hour12: Int,
    val hour13: Int,
    val hour14: Int,
    val hour15: Int,
    val hour16: Int,
    val hour17: Int,
    val hour18: Int,
    val hour19: Int,
    val hour20: Int,
    val hour21: Int,
    val hour22: Int,
    val hour23: Int,
    val totalHour: Int,
    var isCompleted: Boolean
)
