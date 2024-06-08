package com.example.myinfo

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.local.R
import com.example.local.user.UserTokenStore
import com.example.myinfo.api.LoginApiService
import com.example.myinfo.setup.SetupFlag
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val backgroundColor: Color = Color(android.graphics.Color.parseColor("#EFEFEF"))

@Composable
fun IconImage2(
    modifier: Modifier = Modifier,
    imageResId: Int,
    size: Dp,
    cornerRadius: Dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun MyInfoScreen(
    navController: NavController,
    onCheckPermissions: (Context) -> Unit,
    viewModel: MyInfoViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val totalWidth = screenWidth * 0.85f
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
            .padding(top = 18.dp), // 상단에 여백 추가
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "My Page",
            fontSize = 28.sp, // 글자 크기 키움
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 로고 이미지 추가
        IconImage2(
            imageResId = R.drawable.dkapplogo,
            size = totalWidth * 0.45f, // 로고 사이즈 조정
            cornerRadius = 999.dp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp)) // 로고와 텍스트 사이 간격 추가

        // 중앙 정렬 텍스트 추가
        Text(
            text = "당신의 오늘 하루는 생산적이었나요?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp)) // 텍스트와 메뉴 항목 사이 간격 추가

        settingsContent(uiState, navController, viewModel, onCheckPermissions)
    }
    // Back button handler
    BackHandler {
        navController.navigate("overview_route") {
            popUpTo("overview_route") { inclusive = true }
        }
    }
}

@Composable
fun settingsContent(
    uiState: MyInfoUiState,
    navController: NavController,
    viewModel: MyInfoViewModel,
    onCheckPermissions: (Context) -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val totalWidth = screenWidth * 0.85f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Settings(modifier = Modifier, totalWidth = totalWidth, navController, viewModel, onCheckPermissions)
    }
}

@Composable
fun Settings(
    modifier: Modifier,
    totalWidth: Dp,
    navController: NavController,
    viewModel: MyInfoViewModel,
    onCheckPermissions: (Context) -> Unit
) {
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
                contentAlignment = Alignment.Center
            ) {
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
                    .height(totalWidth * 0.16f)
                    .clickable {
                        onCheckPermissions(context)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "권한 설정",
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
                            SetupFlag.resetSetup(context)
                            popUpTo(0) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                    .height(totalWidth * 0.16f),
                contentAlignment = Alignment.Center
            ) {
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
                        val username = UserTokenStore.getUserId(context)
                        if (token != null) {
                            viewModel.deleteUserData("Bearer $token", username) {
                                // After deleting user data, call the user account deletion API
                                val call = LoginApiService.userApi.deleteUser("Bearer $token", username)
                                call.enqueue(object : Callback<Map<String, String>> {
                                    override fun onResponse(
                                        call: Call<Map<String, String>>,
                                        response: Response<Map<String, String>>
                                    ) {
                                        if (response.isSuccessful) {
                                            // Clear token and user ID, then navigate to the main screen
                                            navController.navigate("main_screen") {
                                                UserTokenStore.clearToken(context)
                                                UserTokenStore.clearUserId(context)
                                                popUpTo(0) { inclusive = true }
                                                launchSingleTop = true
                                            }
                                        } else {
                                            // Handle failure
                                        }
                                    }

                                    override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                                        // Handle failure
                                    }
                                })
                            }
                        }
                    }
                    .height(totalWidth * 0.16f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "계정 탈퇴",
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 16.sp)
                )
            }
        }
    }
}
