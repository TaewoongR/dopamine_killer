package com.example.recorddomain.di

import com.example.recorddomain.ReDomain
import com.example.recorddomain.ReDomainImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ReDomainModule {
    @Binds
    @Singleton
    fun bindReDomain(reDomainImpl: ReDomainImpl): ReDomain
}