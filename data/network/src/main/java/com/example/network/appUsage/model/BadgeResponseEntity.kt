package com.example.network.appUsage.model

data class BadgeResponse(
    val id: Int,
    val badgeInfo: BadgeInfo,
    val isAchieved: Boolean
)

data class BadgeInfo(
    val id: Int,
    val name: String,
    val description: String,
    val criteria: Int,
    val imageUrl: String
)
