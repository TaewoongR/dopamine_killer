package com.example.myinfo

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.local.R
import com.example.local.user.UserTokenStore
import com.example.myinfo.api.LoginApiService
import com.example.myinfo.api.UserLogin
import com.example.myinfo.setup.SetupFlag
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LoginScreen(navController: NavController, navigateToMainScreen: () -> Unit) {
    loginContent(navController, navigateToMainScreen)
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun loginContent(navController: NavController, navigateToMainScreen: () -> Unit){
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val totalWidth = screenWidth * 0.85f
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(modifier = Modifier
        .fillMaxSize()
        .background(backgroundColor)
        .pointerInput(Unit) {   // 화면을 터치하면 키보드화면 제거
            detectTapGestures(onTap = {
                focusManager.clearFocus()
                keyboardController?.hide()
            })
        },
        contentAlignment = Alignment.TopCenter){

        IconImage(
            imageResId = R.drawable.dkapplogo,
            size = totalWidth * 0.6f,
            cornerRadius = 999.dp,
            modifier = Modifier.offset( y = totalWidth * 0.3f))
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(totalWidth * 0.08f),
            modifier = Modifier
                .offset(y = totalWidth * 1.06f)
                .width(totalWidth)){
            BasicTextField(
                value = username,
                onValueChange = { username = it },
                textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus() // 엔터를 치면 포커스 제거
                    keyboardController?.hide() // 키보드 숨김
                }),
                decorationBox = {innerTextField ->
                    Box(
                        modifier = Modifier
                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                            .fillMaxWidth()
                            .height(totalWidth * 0.16f)
                            .padding(start = totalWidth * 0.04f, end = totalWidth * 0.04f),
                        contentAlignment = Alignment.CenterStart
                    ){
                        if (username.isEmpty()) {
                            Text("아이디", color = Color.Gray, fontSize = 16.sp)
                        }
                        innerTextField()
                    }
                }
            )
            // 비밀번호 입력 필드
            BasicTextField(
                value = password,
                onValueChange = { password = it },
                textStyle = TextStyle(color = Color.Black, fontSize = 14.sp),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus() // 엔터를 치면 포커스 제거
                    keyboardController?.hide() // 키보드 숨김
                }),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .background(Color.White, shape = RoundedCornerShape(8.dp))
                            .fillMaxWidth()
                            .height(totalWidth * 0.16f)
                            .padding(start = totalWidth * 0.04f, end = totalWidth * 0.04f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (password.isEmpty()) {
                            Text("비밀번호", color = Color.Gray, fontSize = 16.sp)
                        }
                        innerTextField()
                    }
                }
            )

            // 로그인 버튼
            loginButton(totalWidth = totalWidth, username, password, navController, navigateToMainScreen)
            Text(
                text = "비밀번호 찾기",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color(android.graphics.Color.parseColor("#6083FF")),
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter) // 이 부분을 추가하여 하단 중앙에 위치하도록 설정
                .fillMaxWidth()
                .background(Color(android.graphics.Color.parseColor("#636684")))
                .height(totalWidth * 0.26f),
            contentAlignment = Alignment.Center
        ) {
            Row {
                Text(text = "계정이 없으신가요?",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.width(totalWidth * 0.04f))
                Text(text = "회원가입 하기",
                    modifier = Modifier.clickable{
                        navController.navigate("signup_route")
                    },
                    color = Color(android.graphics.Color.parseColor("#8EA7FF")),
                    textDecoration = TextDecoration.Underline,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun IconImage(
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
fun loginButton(totalWidth: Dp, username: String, password: String, navController: NavController, navigateToMainScreen: () -> Unit){
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    if (isLoading) {
        CircularProgressIndicator()
    }else {
        Box(
            modifier = Modifier
                .width(totalWidth)
                .aspectRatio(1f / 0.1875f)
                .background(keyColor, shape = RoundedCornerShape(12.dp))
                .clickable {
                    // 로그인 로직 처리
                    isLoading = true
                    val userLogin = UserLogin(username, password)
                    LoginApiService.userApi
                        .loginUser(userLogin)
                        .enqueue(object : Callback<Map<String, String>> {
                            override fun onResponse(
                                call: Call<Map<String, String>>,
                                response: Response<Map<String, String>>
                            ) {
                                isLoading = false
                                if (response.isSuccessful && response.body() != null) {
                                    response
                                        .body()
                                        ?.get("token")
                                        ?.let { token ->
                                            Log.d("LoginScreen", "Login success with token: $token")
                                            if(response.body()?.get("initialSet") == "true"){
                                                SetupFlag.saveSetupComplete(context)
                                            }
                                            UserTokenStore.saveToken(context, token)
                                            UserTokenStore.saveUserId(context, username)
                                            navigateToMainScreen()
                                        } ?: run {
                                        errorMessage = "로그인 실패: 토큰이 반환되지 않았습니다."
                                        Log.d("LoginScreen", "Login failed: Token not returned")
                                    }
                                } else {
                                    errorMessage = "아이디 또는 비밀번호를 잘못 입력했습니다. 입력하신 내용을 다시 확인해주세요."
                                    Log.d(
                                        "LoginScreen",
                                        "Login failed with error body: ${
                                            response
                                                .errorBody()
                                                ?.string()
                                        }"
                                    )
                                }
                            }
                            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                                isLoading = false
                                Log.e("LoginScreen", "Network error: ${t.message}")
                                errorMessage = "네트워크 에러: ${t.message}"
                            }
                        })
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "로그인",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}
