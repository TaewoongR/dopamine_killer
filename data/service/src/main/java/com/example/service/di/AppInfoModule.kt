package com.example.service.di

import com.example.service.AppFetchingInfo
import com.example.service.AppFetchingInfoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppInfoModule {
    @Binds
    @Singleton
    fun bindAppInfo(appInfoImpl: AppFetchingInfoImpl): AppFetchingInfo
}