package com.example.local.di

import android.content.Context
import androidx.room.Room
import com.example.local.dailyInfo.DailyInfoDAO
import com.example.local.dailyInfo.DailyInfoDatabase
import com.example.local.dailyUsage.DailyDAO
import com.example.local.dailyUsage.DailyDatabase
import com.example.local.horulyUsage.HourlyDAO
import com.example.local.horulyUsage.HourlyDatabase
import com.example.local.monthlyUsage.MonthlyDAO
import com.example.local.monthlyUsage.MonthlyDatabase
import com.example.local.record.RecordDAO
import com.example.local.record.RecordDatabase
import com.example.local.selectedApp.SelectedAppDAO
import com.example.local.selectedApp.SelectedAppDatabase
import com.example.local.user.UserDAO
import com.example.local.user.UserDatabase
import com.example.local.weeklyUsage.WeeklyDAO
import com.example.local.weeklyUsage.WeeklyDatabase
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
    fun provideHourlyDatabase(@ApplicationContext context: Context): HourlyDatabase {
        return Room.databaseBuilder(context, HourlyDatabase::class.java, "HourlyData.db")
            .fallbackToDestructiveMigration() // 데이터베이스 스키마 버전이 변경될 경우 데이터를 삭제하고 다시 시작합니다.
            .build()
    }

    @Provides
    fun provideHourlyDAO(hourlyDatabase: HourlyDatabase): HourlyDAO {
        return hourlyDatabase.hourlyDAO()
    }

    @Provides
    @Singleton
    fun provideDailyDatabase(@ApplicationContext context: Context): DailyDatabase {
        return Room.databaseBuilder(context, DailyDatabase::class.java, "DailyData.db")
            .fallbackToDestructiveMigration() // 데이터베이스 스키마 버전이 변경될 경우 데이터를 삭제하고 다시 시작합니다.
            .build()
    }

    @Provides
    fun provideDailyDAO(dailyDatabase: DailyDatabase): DailyDAO {
        return dailyDatabase.dailyDAO()
    }

    @Provides
    @Singleton
    fun provideWeeklyDatabase(@ApplicationContext context: Context): WeeklyDatabase {
        return Room.databaseBuilder(context, WeeklyDatabase::class.java, "WeeklyData.db")
            .fallbackToDestructiveMigration() // 데이터베이스 스키마 버전이 변경될 경우 데이터를 삭제하고 다시 시작합니다.
            .build()
    }

    @Provides
    fun provideWeeklyDAO(weeklyDatabase: WeeklyDatabase): WeeklyDAO {
        return weeklyDatabase.weeklyDAO()
    }

    @Provides
    @Singleton
    fun provideMonthlyDatabase(@ApplicationContext context: Context): MonthlyDatabase {
        return Room.databaseBuilder(context, MonthlyDatabase::class.java, "MonthlyData.db")
            .fallbackToDestructiveMigration() // 데이터베이스 스키마 버전이 변경될 경우 데이터를 삭제하고 다시 시작합니다.
            .build()
    }

    @Provides
    fun provideMonthlyDAO(monthlyDatabase: MonthlyDatabase): MonthlyDAO {
        return monthlyDatabase.monthlyDAO()
    }

    @Provides
    @Singleton
    fun provideDailyInfoDatabase(@ApplicationContext context: Context): DailyInfoDatabase {
        return Room.databaseBuilder(context, DailyInfoDatabase::class.java, "DailyInfoData.db")
            .fallbackToDestructiveMigration() // 데이터베이스 스키마 버전이 변경될 경우 데이터를 삭제하고 다시 시작합니다.
            .build()
    }

    @Provides
    fun provideDailyInfoDAO(dailyInfoDatabase: DailyInfoDatabase): DailyInfoDAO {
        return dailyInfoDatabase.dailyInfoDAO()
    }

    @Provides
    @Singleton
    fun provideUserDatabase(@ApplicationContext context: Context): UserDatabase {
        return Room.databaseBuilder(context, UserDatabase::class.java, "UserData.db")
            .fallbackToDestructiveMigration() // 데이터베이스 스키마 버전이 변경될 경우 데이터를 삭제하고 다시 시작합니다.
            .build()
    }

    @Provides
    fun provideUserDAO(userDatabase: UserDatabase): UserDAO {
        return userDatabase.userDAO()
    }

    @Provides
    @Singleton
    fun provideRecordDatabase(@ApplicationContext context: Context): RecordDatabase {
        return Room.databaseBuilder(context, RecordDatabase::class.java, "RecordData.db")
            .fallbackToDestructiveMigration() // 데이터베이스 스키마 버전이 변경될 경우 데이터를 삭제하고 다시 시작합니다.
            .build()
    }

    @Provides
    fun provideRecordDAO(recordDatabase: RecordDatabase): RecordDAO {
        return recordDatabase.recordDAO()
    }

    @Provides
    @Singleton
    fun provideSelectedAppDatabase(@ApplicationContext context: Context): SelectedAppDatabase {
        return Room.databaseBuilder(context, SelectedAppDatabase::class.java, "RecordData.db")
            .fallbackToDestructiveMigration() // 데이터베이스 스키마 버전이 변경될 경우 데이터를 삭제하고 다시 시작합니다.
            .build()
    }

    @Provides
    fun provideSelectedAppDAO(selectedAppDatabase: SelectedAppDatabase): SelectedAppDAO {
        return selectedAppDatabase.selectedAppDAO()
    }
}
