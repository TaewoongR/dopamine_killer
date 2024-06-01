package com.example.dopamine_killer.foregroundService

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.PowerManager
import android.util.Log

class ForegroundAppChecker(private val context: Context) {

    fun getForegroundApp(): String? {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (powerManager.isInteractive) {
            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val endTime = System.currentTimeMillis()
            val startTime = endTime - 1000 * 60 * 5  // 5분 간격
            val usageStatsList = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime
            )

            if (usageStatsList != null) {
                val sortedStats = usageStatsList.sortedWith(compareByDescending { it.lastTimeUsed })
                if (sortedStats.isNotEmpty()) {
                    return sortedStats[0].packageName
                }
            }
        } else {
            Log.d("ForegroundAppChecker", "Device is not interactive")
        }
        return null
    }
}
