package com.example.repository.di

import com.example.repository.AppRepository
import com.example.repository.AppRepositoryImpl
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
    fun bindRepository(appRepositoryImpl: AppRepositoryImpl): AppRepository
}