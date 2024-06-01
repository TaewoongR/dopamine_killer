package com.example.rewardDomain

interface RewardDomain {
    suspend fun getRewardInfo(): List<Triple<String, String, String>>
}