package com.example.repository.di

import com.example.repository.LocalRepository
import com.example.repository.LocalRepositoryImpl
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
    fun bindRepository(appRepositoryImpl: LocalRepositoryImpl): LocalRepository
}