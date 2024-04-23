package com.example.service.di

import com.example.service.DateFactory
import com.example.service.DateFactoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DateFactoryModule {
    @Binds
    @Singleton
    fun bindDateFactory(dateFactoryImpl: DateFactoryImpl): DateFactory
}