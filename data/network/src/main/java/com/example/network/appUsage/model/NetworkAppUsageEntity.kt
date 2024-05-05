package com.example.network.appUsage.model

import com.example.local.appUsage.AppUsageEntity
import kotlinx.serialization.Serializable

@Serializable
data class NetworkAppUsageEntity(
    val id: String,
    val appName: String,
    val date: String,
    val dayOfWeek: Int = 0, // Calendar 라이브러리 일 = 1, 토 = 7
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
    val isCompleted: Boolean
)

fun NetworkAppUsageEntity.asEntity() = AppUsageEntity(
    appName = appName,
    date = date,
    dayOfWeek = dayOfWeek,
    hour00 = hour00,
    hour01 = hour01,
    hour02 = hour02,
    hour03 = hour03,
    hour04 = hour04,
    hour05 = hour05,
    hour06 = hour06,
    hour07 = hour07,
    hour08 = hour08,
    hour09 = hour09,
    hour10 = hour10,
    hour11 = hour11,
    hour12 = hour12,
    hour13 = hour13,
    hour14 = hour14,
    hour15 = hour15,
    hour16 = hour16,
    hour17 = hour17,
    hour18 = hour18,
    hour19 = hour19,
    hour20 = hour20,
    hour21 = hour21,
    hour22 = hour22,
    hour23 = hour23,
    totalHour = totalHour,
    isCompleted = isCompleted
)