package com.example.service.di

import com.example.service.AppInfo
import com.example.service.AppInfoImpl
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
    fun bindAppInfo(appInfoImpl: AppInfoImpl): AppInfo
}