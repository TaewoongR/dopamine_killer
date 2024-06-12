package com.example.navigation.initialSetting

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myinfo.backgroundColor
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun PermissionScreen(navController: NavController) {
    val context = LocalContext.current
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val hasPermission = remember { mutableStateOf(false) }

    // Continuously check the permission status
    LaunchedEffect(key1 = true) {
        while (true) {
            val mode = appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
            hasPermission.value = (mode == AppOpsManager.MODE_ALLOWED)
            if (hasPermission.value) {
                navController.navigate("app_setting")
                break
            }
            delay(1)
        }
    }
    accessPermissionContent(navController, context)
}

@Composable
fun accessPermissionContent(navController: NavController, context: Context){
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val totalWidth = screenWidth * 0.85f

    Box(modifier = Modifier
        .background(backgroundColor)
        .fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column (horizontalAlignment = Alignment.CenterHorizontally){
            Text(
                text = "스크린타임 권한 요청",
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = totalWidth * 0.16f)
            )
            Box(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(
                        start = totalWidth * 0.1f,
                        end = totalWidth * 0.1f,
                        top = totalWidth * 0.16f,
                        bottom = totalWidth * 0.16f
                    )
            ){
                Column (verticalArrangement = Arrangement.spacedBy(4.dp)){
                    Text( // bold체 적용을 위해 메인 텍스트 작성 코드 변경
                        text = buildAnnotatedString {
                            append("사용시간 분석을 위해 \n")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("스크린타임 권한")
                            }
                            append("이 필요합니다.\n")
                        },
                        style = TextStyle(fontSize = 14.sp, lineHeight = 24.sp)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "bullet", // 접근성을 위한 설명
                            tint = Color.Black,
                            modifier = Modifier.size(10.dp)
                        )
                        Text(text = " 권한을 허용하면 목표 앱 사용에 대한 기록이 가능합니다.", style = TextStyle(fontSize = 11.sp))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "bullet", // 접근성을 위한 설명
                            tint = Color.Black,
                            modifier = Modifier.size(10.dp)
                        )
                        Text(text = " [설정] > [디지털 웰빌 설정] > [사용정보 접근 허용]으로", style = TextStyle(fontSize = 11.sp))
                    }
                    Text(text = "     언제든 권한을 해지할 수 있습니다. ", style = TextStyle(fontSize = 11.sp))
                }
            }
            Spacer(modifier = Modifier.height(totalWidth * 0.8f))
        }
        Box(modifier = Modifier.offset(y = totalWidth * 1f)){
            setButton(totalWidth = totalWidth, navController, context)
        }
    }
}

@Composable
fun setButton(totalWidth: Dp, navController: NavController, context: Context){
    Box(
        modifier = Modifier
            .width(totalWidth)
            .aspectRatio(1f / 0.1875f)
            .background(com.example.record.keyColor, shape = RoundedCornerShape(12.dp))
            .clickable {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS, Uri.parse("package:${context.packageName}"))
                context.startActivity(intent)
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = "권한 허용", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White))
    }
}