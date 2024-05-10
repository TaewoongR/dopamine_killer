package com.example.local.di

import com.example.local.selectedApp.AppNameStorage
import com.example.local.selectedApp.AppNameStorageInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface StorageModule{
    @Binds
    @Singleton
    fun bindAppNameStorage(appNameStorage: AppNameStorage): AppNameStorageInterface
}