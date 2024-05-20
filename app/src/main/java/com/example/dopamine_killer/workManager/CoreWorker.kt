package com.example.dopamine_killer.workManager

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.coredomain.CoreDomain
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class CoreWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val coreDomain: CoreDomain
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "CoreWorker"
    }

    override suspend fun doWork(): Result {
        val taskType = inputData.getString("TASK_TYPE") ?: "DEFAULT"
        Log.d(TAG, "Task type: $taskType")

        return withContext(Dispatchers.IO) {
            try {
                when (taskType) {
                    "UPDATE_HOURLY_DAILY_USAGE" -> {
                        Log.d(TAG, "Updating hourly daily usage")
                        coreDomain.updateHourlyDailyUsage()
                    }
                    "UPDATE_WEEKLY_USAGE" -> {
                        Log.d(TAG, "Updating weekly usage")
                        coreDomain.updateWeeklyUsage()
                    }
                    "UPDATE_INSTALLED_APP" -> {
                        Log.d(TAG, "Updating installed apps")
                        updateInstalledApp()
                        coreDomain.postNetworkHourly()
                    }
                    "POST_NETWORK_HOURLY" -> {
                        Log.d(TAG, "Posting network hourly")
                    }
                    else -> {
                        Log.d(TAG, "Executing default task")
                        defaultTask()
                    }
                }
                Log.d(TAG, "Task completed successfully")
                Result.success()
            } catch (e: Exception) {
                Log.e(TAG, "Task failed", e)
                Result.failure()
            }
        }
    }

    private suspend fun updateInstalledApp() {
        try {
            val packageInfo = applicationContext.packageManager.getPackageInfo("com.example.dopamine_killer", 0)
            Log.d(TAG, "Package found: ${packageInfo.packageName}")
            withContext(Dispatchers.IO) {coreDomain.updatePeriodicInstalledApp()}
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Package not found: com.example.dopamine_killer", e)
        }
    }

    private fun defaultTask() {
        Log.d(TAG, "Executing default task logic")
        // 기본 작업 수행
    }
}
