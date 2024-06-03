package com.example.dopamine_killer

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
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
                Log.d("initial", "initial update")
                initialUpdate()
            }
        }

        lifecycle.addObserver(MainActivityLifecycleObserver())
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

    private fun startWorkManagerChain() {
        val workManager = WorkManager.getInstance(this)

        val updateInstalledAppRequest = OneTimeWorkRequestBuilder<CoreWorker>()
            .setInputData(workDataOf("TASK_TYPE" to "UPDATE_INSTALLED_APP"))
            .build()

        val updateHourlyDailyRequest = OneTimeWorkRequestBuilder<CoreWorker>()
            .setInputData(workDataOf("TASK_TYPE" to "UPDATE_HOURLY_DAILY_USAGE"))
            .build()

        val updateWeeklyRequest = OneTimeWorkRequestBuilder<CoreWorker>()
            .setInputData(workDataOf("TASK_TYPE" to "UPDATE_WEEKLY_USAGE"))
            .build()

        val updateAccessGoalRequest = OneTimeWorkRequestBuilder<CoreWorker>()
            .setInputData(workDataOf("TASK_TYPE" to "UPDATE_ACCESS_GOAL"))
            .build()

        val postNetworkHourlyRequest = OneTimeWorkRequestBuilder<CoreWorker>()
            .setInputData(workDataOf("TASK_TYPE" to "POST_NETWORK_HOURLY"))
            .build()

        val postNetworkDailyRequest = OneTimeWorkRequestBuilder<CoreWorker>()
            .setInputData(workDataOf("TASK_TYPE" to "POST_NETWORK_DAILY"))
            .build()

        val postNetworkWeeklyRequest = OneTimeWorkRequestBuilder<CoreWorker>()
            .setInputData(workDataOf("TASK_TYPE" to "POST_NETWORK_WEEKLY"))
            .build()

        val postNetworkGoalRequest = OneTimeWorkRequestBuilder<CoreWorker>()
            .setInputData(workDataOf("TASK_TYPE" to "POST_NETWORK_GOAL"))
            .build()

        // WorkManager 작업 체인 설정
        workManager
            .beginUniqueWork(
                "UsageUpdateChain",
                ExistingWorkPolicy.REPLACE,
                updateInstalledAppRequest
            )
            .then(updateWeeklyRequest)
            .then(updateAccessGoalRequest)
            .enqueue()
    }

    inner class MainActivityLifecycleObserver : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            Log.d("MainActivity", "App moved to foreground")
            startWorkManagerChain()
        }
    }
}