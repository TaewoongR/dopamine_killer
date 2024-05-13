package com.example.myinfo

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myinfo.api.ApiService
import com.example.myinfo.api.UserLogin
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LoginScreen(navController: NavController, navigateToMainScreen: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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