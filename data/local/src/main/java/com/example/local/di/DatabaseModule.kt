// DataModule.kt
package com.example.local.di

import android.content.Context
import androidx.room.Room
import com.example.local.dailyUsage.DailyDAO
import com.example.local.horulyUsage.HourlyDAO
import com.example.local.monthlyUsage.MonthlyDAO
import com.example.local.record.RecordDAO
import com.example.local.selectedApp.SelectedAppDAO
import com.example.local.weeklyUsage.WeeklyDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DopamineDatabase {
        return Room.databaseBuilder(
            context,
            DopamineDatabase::class.java,
            "DopamineData.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideDailyDAO(database: DopamineDatabase): DailyDAO {
        return database.dailyDAO()
    }

    @Provides
    fun provideHourlyDAO(database: DopamineDatabase): HourlyDAO {
        return database.hourlyDAO()
    }

    @Provides
    fun provideMonthlyDAO(database: DopamineDatabase): MonthlyDAO {
        return database.monthlyDAO()
    }

    @Provides
    fun provideRecordDAO(database: DopamineDatabase): RecordDAO {
        return database.recordDAO()
    }

    @Provides
    fun provideSelectedAppDAO(database: DopamineDatabase): SelectedAppDAO {
        return database.selectedAppDAO()
    }

    @Provides
    fun provideWeeklyDAO(database: DopamineDatabase): WeeklyDAO {
        return database.weeklyDAO()
    }
}
