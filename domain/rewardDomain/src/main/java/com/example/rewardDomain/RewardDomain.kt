package com.example.rewardDomain

interface RewardDomain {
    suspend fun getRewardInfo(username: String): List<Triple<String, String, String>>
}