package com.example.dopamine_killer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.dopamine_killer.workManager.CoreWorker
import com.example.navigation.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
        scheduleUpdateHourlyDaily()
        scheduleUpdateInstalledApp()
    }

    private fun scheduleUpdateInstalledApp() {
        val updateInstalledAppRequest = OneTimeWorkRequestBuilder<CoreWorker>()
            .setInputData(workDataOf("TASK_TYPE" to "UPDATE_INSTALLED_APP"))
            .build()

        WorkManager.getInstance(this).enqueueUniqueWork(
            "UpdateInstalledApp",
            ExistingWorkPolicy.REPLACE,
            updateInstalledAppRequest
        )
    }

    private fun scheduleUpdateHourlyDaily() {
        val updateHourlyDailyRequest = OneTimeWorkRequestBuilder<CoreWorker>()
            .setInputData(workDataOf("TASK_TYPE" to "UPDATE_HOURLY_DAILY_USAGE"))
            .build()

        WorkManager.getInstance(this).enqueueUniqueWork(
            "UpdateHourlyDailyUsage",
            ExistingWorkPolicy.REPLACE,
            updateHourlyDailyRequest
        )
    }

    private fun schedulePostNetworkHourly() {
        val updateNetworkHourlyRequest = OneTimeWorkRequestBuilder<CoreWorker>()
            .setInputData(workDataOf("TASK_TYPE" to "POST_NETWORK_HOURLY"))
            .build()

        WorkManager.getInstance(this).enqueueUniqueWork(
            "PostNetworkHourly",
            ExistingWorkPolicy.REPLACE,
            updateNetworkHourlyRequest
        )
    }
}