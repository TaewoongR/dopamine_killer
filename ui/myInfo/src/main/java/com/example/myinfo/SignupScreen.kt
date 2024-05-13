package com.example.myinfo

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myinfo.api.ApiService
import com.example.myinfo.api.UserSignup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun SignUpScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var job by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val listGenders = listOf("남자", "여자")
    val listJobs = listOf("학생", "무직")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
            label = { Text("사용자 아이디") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("사용자 이메일") },
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
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("비밀번호 확인") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            maxLines = 1
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("사용자 이름") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1
        )
        OutlinedTextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text("사용자 별명") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1
        )
        OutlinedTextField(
            value = age,
            onValueChange = { if (it.all { char -> char.isDigit() }) age = it },
            label = { Text("사용자 나이") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // 드롭다운 메뉴 구현
        DropdownField(label = "성별", value = gender, options = listGenders) { gender = it }
        DropdownField(label = "직업", value = job, options = listJobs) { job = it }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                // 회원가입 로직 처리
                if (password != confirmPassword) {
                    errorMessage = "비밀번호가 일치하지 않습니다."
                    return@Button
                }
                isLoading = true
                val userSignup = UserSignup(
                    username = username,
                    email = email,
                    password = password,
                    name = name,
                    nickname = nickname,
                    age = age.toInt(),
                    gender = gender,
                    job = job
                )
                ApiService.userApi.signupUser(userSignup).enqueue(object :
                    Callback<Map<String, String>> {
                    override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                        isLoading = false
                        if (response.isSuccessful) {
                            // 회원가입 성공
                            Log.d("SignUpScreen", "Signup success")
                            navController.popBackStack()
                        } else {
                            // 회원가입 실패
                            errorMessage = "회원가입 실패: ${response.errorBody()?.string() ?: "Unknown error"}"
                            Log.d("SignUpScreen", "Signup failed: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                        isLoading = false
                        errorMessage = "네트워크 에러: ${t.message}"
                        Log.e("SignUpScreen", "Network error: ${t.message}")
                    }
                })
                //navController.popBackStack() // 회원가입 후 이전 화면으로 돌아가기
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("회원가입 완료")
        }
    }
}

@Composable
fun DropdownField(label: String, value: String, options: List<String>, onValueChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = value,
            onValueChange = { },
            label = { Text(label) },
            readOnly = true,
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, Modifier.clickable { expanded = !expanded }) },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth() // Ensure the dropdown menu is the same width as the TextField
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    },
                    text = { Text(option) } // Correct use of text parameter
                )
            }
        }
    }
}