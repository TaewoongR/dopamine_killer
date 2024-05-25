package com.example.rewardDomain.di

import com.example.rewardDomain.RewardDomain
import com.example.rewardDomain.RewardDomainImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RewardDomainModule {
    @Binds
    @Singleton
    fun bindRewardDomain(rewardDomainImpl: RewardDomainImpl): RewardDomain
}