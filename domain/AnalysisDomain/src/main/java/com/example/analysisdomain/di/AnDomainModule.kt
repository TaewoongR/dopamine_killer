package com.example.analysisdomain.di

import com.example.analysisdomain.AnDomain
import com.example.analysisdomain.AnDomainImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AnDomainModule {
    @Binds
    @Singleton
    fun bindAnDomain(anDomainImpl: AnDomainImpl): AnDomain
}