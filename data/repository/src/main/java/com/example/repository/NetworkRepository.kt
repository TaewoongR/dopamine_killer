package com.example.repository

interface NetworkRepository {
    suspend fun updateEntireNetworkHourly()
}