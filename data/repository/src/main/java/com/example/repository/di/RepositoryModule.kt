package com.example.repository.di

import com.example.repository.DailyRepository
import com.example.repository.DailyRepositoryImpl
import com.example.repository.GoalRepository
import com.example.repository.GoalRepositoryImpl
import com.example.repository.MonthlyRepository
import com.example.repository.MonthlyRepositoryImpl
import com.example.repository.NetworkRepository
import com.example.repository.NetworkRepositoryImpl
import com.example.repository.SelectedAppRepository
import com.example.repository.SelectedAppRepositoryImpl
import com.example.repository.WeeklyRepository
import com.example.repository.WeeklyRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    @Singleton
    fun bindDailyRepository(dailyRepositoryImpl: DailyRepositoryImpl): DailyRepository

    @Binds
    @Singleton
    fun bindWeeklyRepository(weeklyRepositoryImpl: WeeklyRepositoryImpl): WeeklyRepository

    @Binds
    @Singleton
    fun bindMonthlyRepository(monthlyRepositoryImpl: MonthlyRepositoryImpl): MonthlyRepository

    @Binds
    @Singleton
    fun bindNetworkRepository(networkRepositoryImpl: NetworkRepositoryImpl): NetworkRepository

    @Binds
    @Singleton
    fun bindGoalRepository(goalRepositoryImpl: GoalRepositoryImpl): GoalRepository

    @Binds
    @Singleton
    fun bindSelectedAppRepository(selectedAppRepositoryImpl: SelectedAppRepositoryImpl): SelectedAppRepository
}