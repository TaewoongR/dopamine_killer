package com.example.myinfo

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myInfo.R
import com.example.myinfo.api.ApiService
import com.example.myinfo.api.UserLogin
import com.example.myinfo.utiil.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LoginScreen(navController: NavController, navigateToMainScreen: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        }

        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("사용자 이름") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                // 로그인 로직 처리
                isLoading = true
                val userLogin = UserLogin(username, password)
                ApiService.userApi.loginUser(userLogin).enqueue(object : Callback<Map<String, String>> {
                    override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                        isLoading = false
                        if (response.isSuccessful && response.body() != null) {
                            response.body()?.get("token")?.let { token ->
                                Log.d("LoginScreen", "Login success with token: $token")
                                TokenManager.saveToken(context, token)
                                navigateToMainScreen()
                            } ?: run {
                                errorMessage = "로그인 실패: 토큰이 반환되지 않았습니다."
                                Log.d("LoginScreen", "Login failed: Token not returned")
                            }
                        } else {
                            errorMessage = "로그인 실패: ${response.errorBody()?.string() ?: "Unknown error"}"
                            Log.d("LoginScreen", "Login failed with error body: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                        isLoading = false
                        Log.e("LoginScreen", "Network error: ${t.message}")
                        errorMessage = "네트워크 에러: ${t.message}"
                    }
                })
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("로그인")
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun loginContent(){
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val totalWidth = screenWidth * 0.85f
    var text by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(modifier = Modifier
        .fillMaxSize()
        .background(backgroundColor),
        contentAlignment = Alignment.TopCenter){
        IconImage(
            imageResId = R.drawable.dkapplogo,
            size = totalWidth * 0.6f,
            cornerRadius = 999.dp,
            modifier = Modifier.offset( y = totalWidth * 0.4f))
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(totalWidth * 0.1f),
            modifier = Modifier.offset(y = totalWidth * 1.2f).width(totalWidth)){
            BasicTextField(
                value = text,
                onValueChange = { text = it },
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
                        innerTextField()
                    }
                }
            )
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

@Preview
@Composable
fun DefaultPreview() {
    loginContent()
}