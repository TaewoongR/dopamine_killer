package com.example.dopamine_killer

import android.app.AppOpsManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.coredomain.CoreDomain
import com.example.dopamine_killer.permission.PermissionUtils
import com.example.dopamine_killer.workManager.CoreWorker
import com.example.local.user.UserTokenStore
import com.example.navigation.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var coreDomain: CoreDomain

    private lateinit var appOps: AppOpsManager
    private var mode: Int = AppOpsManager.MODE_ERRORED
    private var token: String? = null
    private val permissionChecker = PermissionUtils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // onCreate 내에서 시스템 서비스와 토큰을 초기화합니다.
        appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        mode = appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), packageName)
        token = try {
            UserTokenStore.getToken(this)
        } catch (e: NullPointerException) {
            null
        }

        setContent {
            MainScreen({ context ->
                permissionChecker.checkAndRequestPermissions(context)
            })
        }
        if (mode == AppOpsManager.MODE_ALLOWED) {
            lifecycle.addObserver(MainActivityLifecycleObserver())
        }
    }

    private fun startWorkManagerChain() {
        val workManager = WorkManager.getInstance(this)

        val updateInstalledAppRequest = OneTimeWorkRequestBuilder<CoreWorker>()
            .setInputData(workDataOf("TASK_TYPE" to "UPDATE_INSTALLED_APP"))
            .build()

        val updateAccessGoalRequest = OneTimeWorkRequestBuilder<CoreWorker>()
            .setInputData(workDataOf("TASK_TYPE" to "UPDATE_ACCESS_GOAL"))
            .build()

        // WorkManager 작업 체인 설정
        workManager
            .beginUniqueWork(
                "CheckAppOnDevice",
                ExistingWorkPolicy.REPLACE,
                updateInstalledAppRequest
            )
            .then(updateAccessGoalRequest)
            .enqueue()
    }

    private fun postServerWorkManagerChain() {
        val workManager = WorkManager.getInstance(this)

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
                "Post Network",
                ExistingWorkPolicy.REPLACE,
                postNetworkHourlyRequest
            )
            .then(postNetworkDailyRequest)
            .then(postNetworkWeeklyRequest)
            .then(postNetworkGoalRequest)
            .enqueue()
    }

    inner class MainActivityLifecycleObserver : DefaultLifecycleObserver {
        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            Log.d("MainActivity", "App moved to foreground")
            startWorkManagerChain()
            if (token != null) {
                postServerWorkManagerChain()
            }
        }
    }

}
