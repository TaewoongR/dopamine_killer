package com.example.dopamine_killer.workManager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.coredomain.CoreDomain
import javax.inject.Inject

class CoreWorker @Inject constructor(
    context: Context,
    workerParams: WorkerParameters,
    private val coreDomain: CoreDomain
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        // 입력 데이터 가져오기
        val taskType = inputData.getString("TASK_TYPE") ?: "DEFAULT"

        return when (taskType) {
            "UPDATE_HOURLY_DAILY_USAGE" -> updateHourlyDailyUsage()
            "UPDATE_WEEKLY_USAGE" -> updateWeeklyUsage()
            "UPDATE_MONTHLY_USAGE" -> updateMonthlyUsage()
            "UPDATE_INSTALLED_APP" -> updateInstalledApp()
            else -> defaultTask()
        }


    }

    private fun updateHourlyDailyUsage(): Result {
        // 작업 A 수행
        return Result.success()
    }

    private fun updateWeeklyUsage(): Result {
        // 작업 B 수행
        return Result.success()
    }

    private fun updateMonthlyUsage(): Result {
        // 작업 B 수행
        return Result.success()
    }

    private fun updateInstalledApp(): Result {
        // 작업 B 수행
        return Result.success()
    }

    private fun defaultTask(): Result {
        // 기본 작업 수행
        return Result.success()
    }
}