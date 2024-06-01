package com.example.myinfo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.local.user.UserTokenStore
import com.example.myinfo.api.LoginApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val backgroundColor: Color = Color(android.graphics.Color.parseColor("#EFEFEF"))

@Composable
fun MyInfoScreen(
    navController: NavController,
    viewModel: MyInfoViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    settingsContent(uiState, navController)
    // Back button handler
    BackHandler {
        navController.navigate("overview_route") {
            popUpTo("overview_route") { inclusive = true }
        }
    }
}

@Composable
fun settingsContent (uiState: MyInfoUiState, navController: NavController){
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val totalWidth = screenWidth * 0.85f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor),
        contentAlignment = Alignment.Center
    ){
        Settings(modifier = Modifier, totalWidth = totalWidth, navController)
    }
}

@Composable
fun Settings(modifier: Modifier, totalWidth: Dp, navController: NavController) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .width(totalWidth)
            .wrapContentHeight()
            .background(Color.White, shape = RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("selected_app_edit") {
                            popUpTo("myinfo_route") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                    .height(totalWidth * 0.16f),
                contentAlignment = Alignment.Center) {
                Text(
                    text = "앱 선택",
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 16.sp)
                )
            }
            HorizontalDivider(color = Color.LightGray, thickness = 0.6.dp, modifier = Modifier.width(totalWidth * 0.8f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(totalWidth * 0.16f),
                contentAlignment = Alignment.Center) {
                Text(
                    text = "프로필 설정",
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 16.sp)
                )
            }
            HorizontalDivider(color = Color.LightGray, thickness = 0.6.dp, modifier = Modifier.width(totalWidth * 0.8f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate("main_screen") {
                            UserTokenStore.clearToken(context)
                            UserTokenStore.clearUserId(context)
                            popUpTo(0) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                    .height(totalWidth * 0.16f), contentAlignment = Alignment.Center) {
                Text(
                    text = "로그아웃",
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 16.sp)
                )
            }
            HorizontalDivider(color = Color.LightGray, thickness = 0.6.dp, modifier = Modifier.width(totalWidth * 0.8f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val token = UserTokenStore.getToken(context)
                        val username = "현재_사용자_이름" // 실제로 현재 사용자의 이름을 넣어야 합니다.

                        if (token != null) {
                            // 회원탈퇴 API 호출
                            val call = LoginApiService.userApi.deleteUser("Bearer $token", username)
                            call.enqueue(object : Callback<Map<String, String>> {
                                override fun onResponse(
                                    call: Call<Map<String, String>>,
                                    response: Response<Map<String, String>>
                                ) {
                                    if (response.isSuccessful) {
                                        // 토큰 삭제 및 로그인 화면으로 네비게이션
                                        navController.navigate("main_screen") { // 대상 루트로 변경하세요
                                            UserTokenStore.clearToken(context)
                                            UserTokenStore.clearUserId(context)
                                            popUpTo(0) {
                                                inclusive = true
                                            }
                                            launchSingleTop = true
                                        }
                                    } else {
                                        // 실패 처리 (예: 오류 메시지 표시)
                                    }
                                }

                                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                                    // 실패 처리 (예: 네트워크 오류 메시지 표시)
                                }
                            })
                        }
                    }
                    .height(totalWidth * 0.16f), contentAlignment = Alignment.Center) {
                Text(
                    text = "계정 탈퇴",
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 16.sp)
                )
            }
        }
    }
}