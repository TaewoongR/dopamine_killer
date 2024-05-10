package com.example.coredomain

interface CoreDomain {
    fun updateSelectedApp()     // 0순위
    fun updateEntireAppUsage()  // 1순위
    fun updateDailyInfo()       // 2순위
}