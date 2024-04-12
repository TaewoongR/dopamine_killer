package com.example.repository

import android.content.Context
import androidx.room.Room
import com.example.local.AppDAO
import com.example.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "YourDatabaseName")
            .fallbackToDestructiveMigration() // 데이터베이스 스키마 버전이 변경될 경우 데이터를 삭제하고 다시 시작합니다.
            .build()
    }

    @Provides
    fun provideAppDAO(appDatabase: AppDatabase): AppDAO {
        return appDatabase.appDAO()
    }
}
