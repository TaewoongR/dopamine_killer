package com.example.local.horulyUsage

import androidx.room.Entity

@Entity(
    tableName = "hourly_usage", primaryKeys = ["appName","date"])
data class HourlyEntity(
    val appName: String,
    val date: String,
    val dayOfWeek: Int,
    val hour00: Int = 0, val hour01: Int = 0, val hour02: Int = 0, val hour03: Int = 0,
    val hour04: Int = 0, val hour05: Int = 0, val hour06: Int = 0, val hour07: Int = 0,
    val hour08: Int = 0, val hour09: Int = 0, val hour10: Int = 0, val hour11: Int = 0,
    val hour12: Int = 0, val hour13: Int = 0, val hour14: Int = 0, val hour15: Int = 0,
    val hour16: Int = 0, val hour17: Int = 0, val hour18: Int = 0, val hour19: Int = 0,
    val hour20: Int = 0, val hour21: Int = 0, val hour22: Int = 0, val hour23: Int = 0
){
    fun getHourlyList(): List<Int> {
        return listOf(
            hour00, hour01, hour02, hour03, hour04, hour05, hour06, hour07,
            hour08, hour09, hour10, hour11, hour12, hour13, hour14, hour15,
            hour16, hour17, hour18, hour19, hour20, hour21, hour22, hour23
        )
    }

    fun getDefaultEntity(appName: String, date: String, dateOfWeek: Int): HourlyEntity{
        return HourlyEntity(
            appName = appName,
            date = date,
            dayOfWeek = dateOfWeek,
            hour00, hour01, hour02, hour03, hour04, hour05, hour06, hour07,
            hour08, hour09, hour10, hour11, hour12, hour13, hour14, hour15,
            hour16, hour17, hour18, hour19, hour20, hour21, hour22, hour23
        )
    }
}