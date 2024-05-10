package com.example.local.di

import android.content.Context
import androidx.room.Room
import com.example.local.appUsage.AppDAO
import com.example.local.appUsage.AppDatabase
import com.example.local.dailyInfo.DailyDAO
import com.example.local.dailyInfo.DailyDatabase
import com.example.local.user.UserDAO
import com.example.local.user.UserDatabase
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "AppData.db")
            .fallbackToDestructiveMigration() // 데이터베이스 스키마 버전이 변경될 경우 데이터를 삭제하고 다시 시작합니다.
            .build()
    }

    @Provides
    fun provideAppDAO(appDatabase: AppDatabase): AppDAO {
        return appDatabase.appDAO()
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
    fun provideUserDatabase(@ApplicationContext context: Context): UserDatabase {
        return Room.databaseBuilder(context, UserDatabase::class.java, "UserData.db")
            .fallbackToDestructiveMigration() // 데이터베이스 스키마 버전이 변경될 경우 데이터를 삭제하고 다시 시작합니다.
            .build()
    }

    @Provides
    fun provideUserDAO(userDatabase: UserDatabase): UserDAO {
        return userDatabase.userDAO()
    }

}
