package com.example.local.record

import androidx.room.Entity

@Entity(
    tableName = "record_data", primaryKeys = ["appName","date"])
data class RecordEntity(
    val appName: String,
    val date: String,
    val goalTime: Int,
    val howLong: Int,
    val onGoing: Boolean = true
)

fun RecordEntity.asExternalModel() = RecordData(
    appName = appName,
    date = date,
    goalTime = goalTime,
    howLong = howLong,
    onGoing = onGoing
)