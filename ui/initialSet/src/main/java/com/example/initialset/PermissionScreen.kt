package com.example.initialset

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun PermissionScreen(navController: NavController) {
    val context = LocalContext.current
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val hasPermission = remember { mutableStateOf(false) }

    // Continuously check the permission status
    LaunchedEffect(key1 = true) {
        while (true) {
            val mode = appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
            hasPermission.value = (mode == AppOpsManager.MODE_ALLOWED)
            if (hasPermission.value) {
                navController.navigate("app_setting") {
                    popUpTo("auth_selection_route") { inclusive = true }
                }
                break
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            if (!hasPermission.value) {
                Text(
                    "이 앱은 사용 통계에 대한 접근 권한이 필요합니다.",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 18.sp,
                        color = Color.Black
                    ),
                    modifier = Modifier.padding(16.dp)
                )
                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = keyColor),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "설정으로 이동",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}
