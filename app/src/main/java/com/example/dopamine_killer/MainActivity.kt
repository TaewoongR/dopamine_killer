package com.example.dopamine_killer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.coredomain.CoreDomain
import com.example.dopamine_killer.workManager.CoreWorker
import com.example.local.R
import com.example.navigation.MainScreen
import com.example.navigation.initialSetting.AppSettingData
import com.example.navigation.setup.SetupFlag
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity: ComponentActivity() {

    @Inject
    lateinit var coreDomain: CoreDomain

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
        // 코루틴 스코프 내에서 suspend 함수를 호출
        if (!SetupFlag.isSetupComplete(this)) {
            lifecycleScope.launch {
                initialUpdate()
            }
        }
        scheduleUpdateHourlyDaily()
        scheduleUpdateInstalledApp()
        scheduleUpdateAccessGoal()
        //schedulePostNetworkHourly()
        //schedulePostNetworkDaily()
    }

    private suspend fun initialUpdate(){
        val appNameList = mutableListOf<String>()
        val appObjectList = mutableListOf<AppSettingData>()
        val fields = R.string::class.java.fields // R.string 클래스의 모든 필드를 가져옴
        val prefix = "app_" // 필터링에 사용할 접두사
        for (field in fields) {
            try {
                // 특정 접두사로 시작하는 문자열 리소스만 처리
                if (field.name.startsWith(prefix)) {
                    val appName = field.name.removePrefix(prefix).replace("_", " ") // 접두사를 제거한 이름
                    val icon = withContext(Dispatchers.IO) {coreDomain.getAppIconForAppSetting(appName)}
                    if (icon != null) {
                        appObjectList.add(AppSettingData(appName = appName, icon = icon))
                        appNameList.add(appName)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace() // 예외 처리
            }
        }
        withContext(Dispatchers.IO) {coreDomain.updateInitialInstalledApp(appNameList)}
        withContext(Dispatchers.IO) {coreDomain.initialUpdate(appNameList)}
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

    private fun scheduleUpdateAccessGoal() {
        val updateGoalRequest = OneTimeWorkRequestBuilder<CoreWorker>()
            .setInputData(workDataOf("TASK_TYPE" to "UPDATE_ACCESS_GOAL"))
            .build()

        WorkManager.getInstance(this).enqueueUniqueWork(
            "UpdateAccessGoal",
            ExistingWorkPolicy.REPLACE,
            updateGoalRequest
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

    private fun schedulePostNetworkDaily() {
        val updateNetworkDailyRequest = OneTimeWorkRequestBuilder<CoreWorker>()
            .setInputData(workDataOf("TASK_TYPE" to "POST_NETWORK_DAILY"))
            .build()

        WorkManager.getInstance(this).enqueueUniqueWork(
            "PostNetworkDaily",
            ExistingWorkPolicy.REPLACE,
            updateNetworkDailyRequest
        )
    }
}