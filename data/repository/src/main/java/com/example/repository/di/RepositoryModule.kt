package com.example.repository.di

import com.example.repository.AppRepository
import com.example.repository.AppRepositoryImpl
import com.example.repository.DailyRepository
import com.example.repository.DailyRepositoryImpl
import com.example.repository.NetworkRepository
import com.example.repository.NetworkRepositoryImpl
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
    fun bindAppRepository(appRepositoryImpl: AppRepositoryImpl): AppRepository


    @Binds
    @Singleton
    fun bindDailyRepository(dailyRepositoryImpl: DailyRepositoryImpl): DailyRepository

    @Binds
    @Singleton
    fun bindNetworkRepository(networkRepositoryImpl: NetworkRepositoryImpl): NetworkRepository
}