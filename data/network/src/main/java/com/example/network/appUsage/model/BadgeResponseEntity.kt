package com.example.network.appUsage.model

data class BadgeResponse(
    val id: Int,
    val badge: BadgeInfo,
    val achieved: Boolean
)

data class BadgeInfo(
    val id: Int,
    val name: String,
    val description: String,
    val criteria: Int,
    val image_url: String
)
