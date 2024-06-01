package com.example.myinfo

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myinfo.api.LoginApiService
import com.example.myinfo.api.UserSignup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

    var usernameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var nicknameError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }
    var genderError by remember { mutableStateOf<String?>(null) }
    var jobError by remember { mutableStateOf<String?>(null) }

    val listGenders = listOf("남자", "여자")
    val listJobs = listOf("학생", "직장인", "주부", "무직", "기타")

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
            maxLines = 1,
            isError = usernameError != null,
            trailingIcon = {
                usernameError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("사용자 이메일") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            isError = emailError != null,
            trailingIcon = {
                emailError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            maxLines = 1,
            isError = passwordError != null,
            trailingIcon = {
                passwordError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        )
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("비밀번호 확인") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            maxLines = 1,
            isError = confirmPasswordError != null,
            trailingIcon = {
                confirmPasswordError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("사용자 이름") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            isError = nameError != null,
            trailingIcon = {
                nameError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        )
        OutlinedTextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text("사용자 별명") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            isError = nicknameError != null,
            trailingIcon = {
                nicknameError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        )
        OutlinedTextField(
            value = age,
            onValueChange = { if (it.all { char -> char.isDigit() }) age = it },
            label = { Text("사용자 나이") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            isError = ageError != null,
            trailingIcon = {
                ageError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
            }
        )

        // 드롭다운 메뉴 구현
        DropdownField(label = "성별", value = gender, options = listGenders, isError = genderError != null, errorMessage = genderError ) { gender = it }

        DropdownField(label = "직업", value = job, options = listJobs, isError = jobError != null, errorMessage = jobError) { job = it }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                // 회원가입 로직 처리 Validate inputs
                var valid = true
                if (username.isEmpty()) {
                    usernameError = "아이디를 입력하세요"
                    valid = false
                }
                if (email.isEmpty()) {
                    emailError = "이메일을 입력하세요"
                    valid = false
                }
                if (password.isEmpty()) {
                    passwordError = "비밀번호를 입력하세요"
                    valid = false
                }
                if (confirmPassword.isEmpty()) {
                    confirmPasswordError = "비밀번호 확인을 입력하세요"
                    valid = false
                }
                if (password != confirmPassword) {
                    confirmPasswordError = "비밀번호가 일치하지 않습니다"
                    valid = false
                }
                if (name.isEmpty()) {
                    nameError = "이름을 입력하세요"
                    valid = false
                }
                if (nickname.isEmpty()) {
                    nicknameError = "별명을 입력하세요"
                    valid = false
                }
                if (age.isEmpty()) {
                    ageError = "나이를 입력하세요"
                    valid = false
                }
                if (gender.isEmpty()) {
                    genderError = "성별을 선택하세요"
                    valid = false
                }
                if (job.isEmpty()) {
                    jobError = "직업을 선택하세요"
                    valid = false
                }

                if (!valid) return@Button

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
                LoginApiService.userApi.signupUser(userSignup).enqueue(object :
                    Callback<Map<String, String>> {
                    override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                        isLoading = false
                        if (response.isSuccessful) {
                            // 회원가입 성공
                            Log.d("SignUpScreen", "Signup success")
                            navController.popBackStack()
                        } else {
                            // 회원가입 실패
                            val type = object : TypeToken<Map<String, String>>() {}.type
                            val errorResponse: Map<String, String>? = Gson().fromJson(response.errorBody()?.charStream(), type)
                            errorMessage = errorResponse?.values?.joinToString(", ") ?: "Unknown error"
                            Log.d("SignUpScreen", "Signup failed: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                        isLoading = false
                        errorMessage = "네트워크 에러: ${t.message}"
                        Log.e("SignUpScreen", "Network error: ${t.message}")
                    }
                })
                // 회원가입 후 이전 화면으로 돌아가기
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("회원가입 완료")
        }
    }
}

@Composable
fun DropdownField(label: String, value: String, options: List<String>, isError: Boolean, errorMessage: String?, onValueChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = value,
            onValueChange = { },
            label = { Text(label) },
            readOnly = true,
            isError = isError,
            trailingIcon = {
                if (isError && errorMessage != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, Modifier.clickable { expanded = !expanded })
                    }
                } else {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, Modifier.clickable { expanded = !expanded })
                }
            },
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