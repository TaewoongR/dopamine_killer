package com.example.dopamine_killer

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.analysis.AnalysisScreen

@Composable
fun PermissionScreen() {
    val context = LocalContext.current
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
    val hasPermission = remember { mutableStateOf(mode == AppOpsManager.MODE_ALLOWED) }

    // 권한이 없으면 사용자에게 설정 화면으로 이동하도록 유도
    if (hasPermission.value) {
        AnalysisScreen()
    } else {
        Column {
            Text("이 앱은 사용 통계에 대한 접근 권한이 필요합니다.")
            Button(onClick = {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                context.startActivity(intent)
            }) {
                Text("설정으로 이동")
            }
        }
    }
}
