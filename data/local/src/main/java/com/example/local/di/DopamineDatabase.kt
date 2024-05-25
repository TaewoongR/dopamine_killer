package com.example.local.di

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.local.dailyUsage.DailyDAO
import com.example.local.dailyUsage.DailyEntity
import com.example.local.horulyUsage.HourlyDAO
import com.example.local.horulyUsage.HourlyEntity
import com.example.local.monthlyUsage.MonthlyDAO
import com.example.local.monthlyUsage.MonthlyEntity
import com.example.local.record.RecordDAO
import com.example.local.record.RecordEntity
import com.example.local.selectedApp.SelectedAppDAO
import com.example.local.selectedApp.SelectedAppEntity
import com.example.local.weeklyUsage.WeeklyDAO
import com.example.local.weeklyUsage.WeeklyEntity

@Database(
    entities = [
        DailyEntity::class,
        HourlyEntity::class,
        MonthlyEntity::class,
        RecordEntity::class,
        SelectedAppEntity::class,
        WeeklyEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class DopamineDatabase : RoomDatabase() {
    abstract fun dailyDAO(): DailyDAO
    abstract fun hourlyDAO(): HourlyDAO
    abstract fun monthlyDAO(): MonthlyDAO
    abstract fun recordDAO(): RecordDAO
    abstract fun selectedAppDAO(): SelectedAppDAO
    abstract fun weeklyDAO(): WeeklyDAO
}
