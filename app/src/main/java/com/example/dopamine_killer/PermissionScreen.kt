package com.example.dopamine_killer

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun PermissionScreen() {
    val context = LocalContext.current
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
    val hasPermission = remember { mutableStateOf(mode == AppOpsManager.MODE_ALLOWED) }

    // 권한이 없으면 사용자에게 설정 화면으로 이동하도록 유도
    if (!hasPermission.value) {
        Column {
            Text("이 앱은 사용 통계에 대한 접근 권한이 필요합니다.")
            Button(onClick = {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                context.startActivity(intent)
            }) {
                Text("설정으로 이동")
            }
        }
    } else {
        Text("권한이 이미 허용되었습니다.")
    }
    CheckPermission()
}

@Composable
fun CheckPermission() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycle = lifecycleOwner.lifecycle

    // 이제 lifecycle를 사용하여 원하는 작업을 할 수 있습니다.
    // 예: DisposableEffect를 사용하여 리스너를 추가
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // 필요한 작업 수행
            }
        }
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}