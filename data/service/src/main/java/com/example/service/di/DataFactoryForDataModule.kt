package com.example.service.di

import com.example.service.DateFactoryForData
import com.example.service.DateFactoryForDataImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DateFactoryForDataModule {
    @Binds
    @Singleton
    fun bindDateFactory(dateFactoryForDataImpl: DateFactoryForDataImpl): DateFactoryForData
}