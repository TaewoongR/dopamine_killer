package com.example.coredomain.di

import com.example.coredomain.CoreDomain
import com.example.coredomain.CoreDomainImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CoreDomainModule {
    @Binds
    @Singleton
    fun bindCoreDomain(coreDomainImpl: CoreDomainImpl): CoreDomain
}