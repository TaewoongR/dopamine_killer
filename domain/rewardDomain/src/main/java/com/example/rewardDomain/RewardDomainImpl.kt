package com.example.rewardDomain

import com.example.network.appUsage.NetworkDataSource
import javax.inject.Inject

class RewardDomainImpl @Inject constructor(
    private val networkRepository: NetworkDataSource
): RewardDomain{

    override suspend fun getRewardInfo(username: String): List<Triple<String, String, String>> {
        return networkRepository.getBadge(username)
    }
}